# TEventBus
    minimalism eventbus
    
    
# @Subscribe

```java
        @Subscribe
        private void listenMethod(String event) {
            ...
        }
        
        @Subscribe(topic = "topic")
        private void listenTopicMethod(String event) {
            ...
        }
```

# @Retry

```java
        @Retry(times = 3)
        @Subscribe
        private void listenMethod(String event) {
            ...
        }
```
# @Publish

```java
        @Publish(to = "busName")
        public String login(...) {
            ...
        }
```


# Demo

```java
    @Test
    void test() {
        LocalEventBus localEventBus = new LocalEventBus();
        localEventBus.setSubscriberExceptionHandler((exception, context) -> System.out.println("handle:[" + exception.getMessage() + "]"));
        Subscriber subscriber = new Subscriber();
        localEventBus.register(subscriber);
        localEventBus.post("world", "newUser");
        localEventBus.post("Tom", "oldUser");
        localEventBus.post("Louis");
        localEventBus.unregister(subscriber);
        localEventBus.post("Emily");
    }

    private class Subscriber {

        int i = 0;

        @Subscribe
        private void listenUser(String name) {
            System.out.println(name + " login");
        }

        @Retry(times = 3)
        @Subscribe(topic = "newUser")
        private void listenNewUser(String name) {
            if (i++ < 2) {
                throw new IllegalArgumentException("error hello new user:" + name);
            }
            System.out.println("hello " + name + "， welcome here");
        }

        @Retry(times = 3)
        @Subscribe(topic = "oldUser")
        private void listenOldUser(String name) {
            if (i++ < 2) {
                throw new IllegalArgumentException("error hello old user:" + name);
            }
            System.out.println("hello " + name + "， welcome back");
        }
    }
```