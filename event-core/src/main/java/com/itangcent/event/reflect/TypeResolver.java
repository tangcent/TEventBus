package com.itangcent.event.reflect;

import com.itangcent.event.utils.Assert;
import com.itangcent.event.utils.Collections;
import com.itangcent.event.utils.StringUtils;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Arrays.asList;

public class TypeResolver {

    private final TypeTable typeTable;

    public TypeResolver() {
        this.typeTable = new TypeTable();
    }

    private TypeResolver(TypeTable typeTable) {
        this.typeTable = typeTable;
    }

    /**
     * Returns a resolver that resolves types "covariantly".
     * <p>For example, when resolving {@code List<T>} in the context of {@code ArrayList<?>},
     * {@code <T>} is covariantly resolved to {@code <?>} such that return type of {@code List::get}
     * is {@code <?>}.
     */
    static TypeResolver covariantly(Type contextType) {
        return new TypeResolver().where(TypeMappingIntrospector.getTypeMappings(contextType));
    }

    /**
     * Returns a resolver that resolves types "invariantly".
     *
     * <p>For example, when resolving {@code List<T>} in the context of {@code ArrayList<?>},
     * {@code <T>} cannot be invariantly resolved to {@code <?>} because otherwise the parameter type
     * of {@code List::set} will be {@code <?>} and it'll falsely say any object can be passed into
     * {@code ArrayList<?>::set}.
     *
     * <p>Instead, {@code <?>} will be resolved to a capture in the form of a type variable
     * {@code <capture-of-? extends Object>}, effectively preventing {@code set} from accepting any
     * type.
     */
    static TypeResolver invariantly(Type contextType) {
        Type invariantContext = WildcardCapturer.INSTANCE.capture(contextType);
        return new TypeResolver().where(TypeMappingIntrospector.getTypeMappings(invariantContext));
    }

    /**
     * Returns a new {@code TypeResolver} with type variables in {@code formal} mapping to types in
     * {@code actual}.
     *
     * <p>For example, if {@code formal} is a {@code TypeVariable T}, and {@code actual} is {@code
     * String.class}, then {@code new TypeResolver().where(formal, actual)} will {@linkplain
     * #resolveType resolve} {@code ParameterizedType List<T>} to {@code List<String>}, and resolve
     * {@code Map<T, Something>} to {@code Map<String, Something>} etc. Similarly, {@code formal} and
     * {@code actual} can be {@code Map<K, V>} and {@code Map<String, Integer>} respectively, or they
     * can be {@code E[]} and {@code String[]} respectively, or even any arbitrary combination
     * thereof.
     *
     * @param formal The type whose type variables or itself is mapped to other type(s). It's almost
     *               always a bug if {@code formal} isn't a type variable and contains no type variable. Make
     *               sure you are passing the two parameters in the right order.
     * @param actual The type that the formal type variable(s) are mapped to. It can be or contain yet
     *               other type variables, in which case these type variables will be further resolved if
     *               corresponding mappings exist in the current {@code TypeResolver} instance.
     */
    public TypeResolver where(Type formal, Type actual) {
        Map<TypeVariableKey, Type> mappings = Collections.newHashMap();
        populateTypeMappings(mappings, Objects.requireNonNull(formal), Objects.requireNonNull(actual));
        return where(mappings);
    }

    /**
     * Returns a new {@code TypeResolver} with {@code variable} mapping to {@code type}.
     */
    TypeResolver where(Map<TypeVariableKey, ? extends Type> mappings) {
        return new TypeResolver(typeTable.where(mappings));
    }

    private static void populateTypeMappings(
            final Map<TypeVariableKey, Type> mappings, final Type from, final Type to) {
        if (from.equals(to)) {
            return;
        }
        new TypeVisitor() {
            @Override
            void visitTypeVariable(TypeVariable<?> typeVariable) {
                mappings.put(new TypeVariableKey(typeVariable), to);
            }

            @Override
            void visitWildcardType(WildcardType fromWildcardType) {
                if (!(to instanceof WildcardType)) {
                    return; // okay to say <?> is anything
                }
                WildcardType toWildcardType = (WildcardType) to;
                Type[] fromUpperBounds = fromWildcardType.getUpperBounds();
                Type[] toUpperBounds = toWildcardType.getUpperBounds();
                Type[] fromLowerBounds = fromWildcardType.getLowerBounds();
                Type[] toLowerBounds = toWildcardType.getLowerBounds();
                Assert.isTrue(
                        fromUpperBounds.length == toUpperBounds.length
                                && fromLowerBounds.length == toLowerBounds.length,
                        "Incompatible type: %s vs. %s",
                        fromWildcardType,
                        to);
                for (int i = 0; i < fromUpperBounds.length; i++) {
                    populateTypeMappings(mappings, fromUpperBounds[i], toUpperBounds[i]);
                }
                for (int i = 0; i < fromLowerBounds.length; i++) {
                    populateTypeMappings(mappings, fromLowerBounds[i], toLowerBounds[i]);
                }
            }

            @Override
            void visitParameterizedType(ParameterizedType fromParameterizedType) {
                if (to instanceof WildcardType) {
                    return; // Okay to say Foo<A> is <?>
                }
                ParameterizedType toParameterizedType = expectArgument(ParameterizedType.class, to);
                if (fromParameterizedType.getOwnerType() != null
                        && toParameterizedType.getOwnerType() != null) {
                    populateTypeMappings(
                            mappings, fromParameterizedType.getOwnerType(), toParameterizedType.getOwnerType());
                }
                Assert.equals(
                        fromParameterizedType.getRawType(),
                        toParameterizedType.getRawType(),
                        "Inconsistent raw type: %s vs. %s",
                        fromParameterizedType,
                        to);
                Type[] fromArgs = fromParameterizedType.getActualTypeArguments();
                Type[] toArgs = toParameterizedType.getActualTypeArguments();
                Assert.isTrue(
                        fromArgs.length == toArgs.length,
                        "%s not compatible with %s",
                        fromParameterizedType,
                        toParameterizedType);
                for (int i = 0; i < fromArgs.length; i++) {
                    populateTypeMappings(mappings, fromArgs[i], toArgs[i]);
                }
            }

            @Override
            void visitGenericArrayType(GenericArrayType fromArrayType) {
                if (to instanceof WildcardType) {
                    return; // Okay to say A[] is <?>
                }
                Type componentType = Types.getComponentType(to);
                Assert.notNull(componentType, "%s is not an array type.", to);
                populateTypeMappings(mappings, fromArrayType.getGenericComponentType(), componentType);
            }

            @Override
            void visitClass(Class<?> fromClass) {
                if (to instanceof WildcardType) {
                    return; // Okay to say Foo is <?>
                }
                // Can't map from a raw class to anything other than itself or a wildcard.
                // You can't say "assuming String is Integer".
                // And we don't support "assuming String is T"; user has to say "assuming T is String".
                throw new IllegalArgumentException("No type mapping from " + fromClass + " to " + to);
            }
        }.visit(from);
    }

    /**
     * Resolves all type variables in {@code type} and all downstream types and returns a
     * corresponding type with type variables resolved.
     */
    public Type resolveType(Type type) {
        Assert.notNull(type);
        if (type instanceof TypeVariable) {
            return typeTable.resolve((TypeVariable<?>) type);
        } else if (type instanceof ParameterizedType) {
            return resolveParameterizedType((ParameterizedType) type);
        } else if (type instanceof GenericArrayType) {
            return resolveGenericArrayType((GenericArrayType) type);
        } else if (type instanceof WildcardType) {
            return resolveWildcardType((WildcardType) type);
        } else {
            // if Class<?>, no resolution needed, we are done.
            return type;
        }
    }

    Type[] resolveTypesInPlace(Type[] types) {
        for (int i = 0; i < types.length; i++) {
            types[i] = resolveType(types[i]);
        }
        return types;
    }

    private Type[] resolveTypes(Type[] types) {
        Type[] result = new Type[types.length];
        for (int i = 0; i < types.length; i++) {
            result[i] = resolveType(types[i]);
        }
        return result;
    }

    private WildcardType resolveWildcardType(WildcardType type) {
        Type[] lowerBounds = type.getLowerBounds();
        Type[] upperBounds = type.getUpperBounds();
        return new Types.WildcardTypeImpl(resolveTypes(lowerBounds), resolveTypes(upperBounds));
    }

    private Type resolveGenericArrayType(GenericArrayType type) {
        Type componentType = type.getGenericComponentType();
        Type resolvedComponentType = resolveType(componentType);
        return Types.newArrayType(resolvedComponentType);
    }

    private ParameterizedType resolveParameterizedType(ParameterizedType type) {
        Type owner = type.getOwnerType();
        Type resolvedOwner = (owner == null) ? null : resolveType(owner);
        Type resolvedRawType = resolveType(type.getRawType());

        Type[] args = type.getActualTypeArguments();
        Type[] resolvedArgs = resolveTypes(args);
        return Types.newParameterizedTypeWithOwner(
                resolvedOwner, (Class<?>) resolvedRawType, resolvedArgs);
    }

    private static <T> T expectArgument(Class<T> type, Object arg) {
        try {
            return type.cast(arg);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(arg + " is not a " + type.getSimpleName());
        }
    }

    /**
     * A TypeTable maintains mapping from {@link TypeVariable} to types.
     */
    private static class TypeTable {
        private final Map<TypeVariableKey, Type> map;

        TypeTable() {
            this.map = Collections.newHashMap();
        }

        private TypeTable(Map<TypeVariableKey, Type> map) {
            this.map = map;
        }

        /**
         * Returns a new {@code TypeResolver} with {@code variable} mapping to {@code type}.
         */
        final TypeTable where(Map<TypeVariableKey, ? extends Type> mappings) {
            Map<TypeVariableKey, Type> map = Collections.newHashMap();
            map.putAll(this.map);
            for (Map.Entry<TypeVariableKey, ? extends Type> mapping : mappings.entrySet()) {
                TypeVariableKey variable = mapping.getKey();
                Type type = mapping.getValue();
                Assert.isTrue(!variable.equalsType(type), "Type variable %s bound to itself", variable);
                map.put(variable, type);
            }
            return new TypeTable(map);
        }

        final Type resolve(final TypeVariable<?> var) {
            final TypeTable unguarded = this;
            TypeTable guarded =
                    new TypeTable() {
                        @Override
                        public Type resolveInternal(TypeVariable<?> intermediateVar, TypeTable forDependent) {
                            if (intermediateVar.getGenericDeclaration().equals(var.getGenericDeclaration())) {
                                return intermediateVar;
                            }
                            return unguarded.resolveInternal(intermediateVar, forDependent);
                        }
                    };
            return resolveInternal(var, guarded);
        }

        /**
         * Resolves {@code var} using the encapsulated type mapping. If it maps to yet another
         * non-reified type or has bounds, {@code forDependants} is used to do further resolution, which
         * doesn't try to resolve any type variable on generic declarations that are already being
         * resolved.
         *
         * <p>Should only be called and overridden by {@link #resolve(TypeVariable)}.
         */
        Type resolveInternal(TypeVariable<?> var, TypeTable forDependants) {
            Type type = map.get(new TypeVariableKey(var));
            if (type == null) {
                Type[] bounds = var.getBounds();
                if (bounds.length == 0) {
                    return var;
                }
                Type[] resolvedBounds = new TypeResolver(forDependants).resolveTypes(bounds);
                /*
                 * We'd like to simply create our own TypeVariable with the newly resolved bounds. There's
                 * just one problem: Starting with JDK 7u51, the JDK TypeVariable's equals() method doesn't
                 * recognize instances of our TypeVariable implementation. This is a problem because users
                 * compare TypeVariables from the JDK against TypeVariables returned by TypeResolver. To
                 * work with all JDK versions, TypeResolver must return the appropriate TypeVariable
                 * implementation in each of the three possible cases:
                 *
                 * 1. Prior to JDK 7u51, the JDK TypeVariable implementation interoperates with ours.
                 * Therefore, we can always create our own TypeVariable.
                 *
                 * 2. Starting with JDK 7u51, the JDK TypeVariable implementations does not interoperate
                 * with ours. Therefore, we have to be careful about whether we create our own TypeVariable:
                 *
                 * 2a. If the resolved types are identical to the original types, then we can return the
                 * original, identical JDK TypeVariable. By doing so, we sidestep the problem entirely.
                 *
                 * 2b. If the resolved types are different from the original types, things are trickier. The
                 * only way to get a TypeVariable instance for the resolved types is to create our own. The
                 * created TypeVariable will not interoperate with any JDK TypeVariable. But this is OK: We
                 * don't _want_ our new TypeVariable to be equal to the JDK TypeVariable because it has
                 * _different bounds_ than the JDK TypeVariable. And it wouldn't make sense for our new
                 * TypeVariable to be equal to any _other_ JDK TypeVariable, either, because any other JDK
                 * TypeVariable must have a different declaration or name. The only TypeVariable that our
                 * new TypeVariable _will_ be equal to is an equivalent TypeVariable that was also created
                 * by us. And that equality is guaranteed to hold because it doesn't involve the JDK
                 * TypeVariable implementation at all.
                 */
                if (Types.NativeTypeVariableEquals.NATIVE_TYPE_VARIABLE_ONLY
                        && Arrays.equals(bounds, resolvedBounds)) {
                    return var;
                }
                return Types.newArtificialTypeVariable(
                        var.getGenericDeclaration(), var.getName(), resolvedBounds);
            }
            // in case the type is yet another type variable.
            return new TypeResolver(forDependants).resolveType(type);
        }
    }

    private static final class TypeMappingIntrospector extends TypeVisitor {

        private final Map<TypeVariableKey, Type> mappings = Collections.newHashMap();

        /**
         * Returns type mappings using type parameters and type arguments found in the generic
         * superclass and the super interfaces of {@code contextClass}.
         */
        static Map<TypeVariableKey, Type> getTypeMappings(Type contextType) {
            Assert.notNull(contextType);
            TypeMappingIntrospector introspector = new TypeMappingIntrospector();
            introspector.visit(contextType);
            return introspector.mappings;
        }

        @Override
        void visitClass(Class<?> clazz) {
            visit(clazz.getGenericSuperclass());
            visit(clazz.getGenericInterfaces());
        }

        @Override
        void visitParameterizedType(ParameterizedType parameterizedType) {
            Class<?> rawClass = (Class<?>) parameterizedType.getRawType();
            TypeVariable<?>[] vars = rawClass.getTypeParameters();
            Type[] typeArgs = parameterizedType.getActualTypeArguments();
            Assert.isTrue(vars.length == typeArgs.length);
            for (int i = 0; i < vars.length; i++) {
                map(new TypeVariableKey(vars[i]), typeArgs[i]);
            }
            visit(rawClass);
            visit(parameterizedType.getOwnerType());
        }

        @Override
        void visitTypeVariable(TypeVariable<?> t) {
            visit(t.getBounds());
        }

        @Override
        void visitWildcardType(WildcardType t) {
            visit(t.getUpperBounds());
        }

        private void map(final TypeVariableKey var, final Type arg) {
            if (mappings.containsKey(var)) {
                // Mapping already established
                // This is possible when following both superClass -> enclosingClass
                // and enclosingclass -> superClass paths.
                // Since we follow the path of superclass first, enclosing second,
                // superclass mapping should take precedence.
                return;
            }
            // First, check whether var -> arg forms a cycle
            for (Type t = arg; t != null; t = mappings.get(TypeVariableKey.forLookup(t))) {
                if (var.equalsType(t)) {
                    // cycle detected, remove the entire cycle from the mapping so that
                    // each type variable resolves deterministically to itself.
                    // Otherwise, a F -> T cycle will end up resolving both F and T
                    // nondeterministically to either F or T.
                    for (Type x = arg; x != null; x = mappings.remove(TypeVariableKey.forLookup(x))) {
                    }
                    return;
                }
            }
            mappings.put(var, arg);
        }
    }

    // This is needed when resolving types against a context with wildcards
    // For example:
    // class Holder<T> {
    //   void set(T data) {...}
    // }
    // Holder<List<?>> should *not* resolve the set() method to set(List<?> data).
    // Instead, it should create a capture of the wildcard so that set() rejects any List<T>.
    private static class WildcardCapturer {

        static final WildcardCapturer INSTANCE = new WildcardCapturer();

        private final AtomicInteger id;

        private WildcardCapturer() {
            this(new AtomicInteger());
        }

        private WildcardCapturer(AtomicInteger id) {
            this.id = id;
        }

        final Type capture(Type type) {
            Assert.notNull(type);
            if (type instanceof Class) {
                return type;
            }
            if (type instanceof TypeVariable) {
                return type;
            }
            if (type instanceof GenericArrayType) {
                GenericArrayType arrayType = (GenericArrayType) type;
                return Types.newArrayType(
                        notForTypeVariable().capture(arrayType.getGenericComponentType()));
            }
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Class<?> rawType = (Class<?>) parameterizedType.getRawType();
                TypeVariable<?>[] typeVars = rawType.getTypeParameters();
                Type[] typeArgs = parameterizedType.getActualTypeArguments();
                for (int i = 0; i < typeArgs.length; i++) {
                    typeArgs[i] = forTypeVariable(typeVars[i]).capture(typeArgs[i]);
                }
                return Types.newParameterizedTypeWithOwner(
                        notForTypeVariable().captureNullable(parameterizedType.getOwnerType()),
                        rawType,
                        typeArgs);
            }
            if (type instanceof WildcardType) {
                WildcardType wildcardType = (WildcardType) type;
                Type[] lowerBounds = wildcardType.getLowerBounds();
                if (lowerBounds.length == 0) { // ? extends something changes to capture-of
                    return captureAsTypeVariable(wildcardType.getUpperBounds());
                } else {
                    // TODO(benyu): handle ? super T somehow.
                    return type;
                }
            }
            throw new AssertionError("must have been one of the known types");
        }

        TypeVariable<?> captureAsTypeVariable(Type[] upperBounds) {
            String name =
                    "capture#" + id.incrementAndGet() + "-of ? extends " + StringUtils.join(upperBounds, '&');
            return Types.newArtificialTypeVariable(WildcardCapturer.class, name, upperBounds);
        }

        private WildcardCapturer forTypeVariable(final TypeVariable<?> typeParam) {
            return new WildcardCapturer(id) {
                @Override
                TypeVariable<?> captureAsTypeVariable(Type[] upperBounds) {
                    Set<Type> combined = new LinkedHashSet<>(asList(upperBounds));
                    // Since this is an artifically generated type variable, we don't bother checking
                    // subtyping between declared type bound and actual type bound. So it's possible that we
                    // may generate something like <capture#1-of ? extends Foo&SubFoo>.
                    // Checking subtype between declared and actual type bounds
                    // adds recursive isSubtypeOf() call and feels complicated.
                    // There is no contract one way or another as long as isSubtypeOf() works as expected.
                    combined.addAll(asList(typeParam.getBounds()));
                    if (combined.size() > 1) { // Object is implicit and only useful if it's the only bound.
                        combined.remove(Object.class);
                    }
                    return super.captureAsTypeVariable(combined.toArray(new Type[0]));
                }
            };
        }

        private WildcardCapturer notForTypeVariable() {
            return new WildcardCapturer(id);
        }

        private Type captureNullable(Type type) {
            if (type == null) {
                return null;
            }
            return capture(type);
        }
    }

    /**
     * Wraps around {@code TypeVariable<?>} to ensure that any two type variables are equal as long as
     * they are declared by the same {@link java.lang.reflect.GenericDeclaration} and have the same
     * name, even if their bounds differ.
     *
     * <p>While resolving a type variable from a {@code var -> type} map, we don't care whether the
     * type variable's bound has been partially resolved. As long as the type variable "identity"
     * matches.
     *
     * <p>On the other hand, if for example we are resolving {@code List<A extends B>} to {@code
     * List<A extends String>}, we need to compare that {@code <A extends B>} is unequal to {@code <A
     * extends String>} in order to decide to use the transformed type instead of the original type.
     */
    static final class TypeVariableKey {
        private final TypeVariable<?> var;

        TypeVariableKey(TypeVariable<?> var) {
            this.var = Objects.requireNonNull(var);
        }

        @Override
        public int hashCode() {
            return Objects.hash(var.getGenericDeclaration(), var.getName());
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof TypeVariableKey) {
                TypeVariableKey that = (TypeVariableKey) obj;
                return equalsTypeVariable(that.var);
            } else {
                return false;
            }
        }

        @Override
        public String toString() {
            return var.toString();
        }

        /**
         * Wraps {@code t} in a {@code TypeVariableKey} if it's a type variable.
         */
        static TypeVariableKey forLookup(Type t) {
            if (t instanceof TypeVariable) {
                return new TypeVariableKey((TypeVariable<?>) t);
            } else {
                return null;
            }
        }

        /**
         * Returns true if {@code type} is a {@code TypeVariable} with the same name and declared by the
         * same {@code GenericDeclaration}.
         */
        boolean equalsType(Type type) {
            if (type instanceof TypeVariable) {
                return equalsTypeVariable((TypeVariable<?>) type);
            } else {
                return false;
            }
        }

        private boolean equalsTypeVariable(TypeVariable<?> that) {
            return var.getGenericDeclaration().equals(that.getGenericDeclaration())
                    && var.getName().equals(that.getName());
        }
    }
}
