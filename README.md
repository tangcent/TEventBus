# TEventBus
    minimalism eventbus
    
    
# @Subscribe

```java
        @Subscribe
        private void listenMethod(String event) {
            ...
        }
        
        @Subscribe(topic = "topic", on = "localEventBus", concurrency = 3, priority = Thread.MAX_PRIORITY)
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
        @Publish(to = "busName", event = "#arg1+','+#arg2", to = "localEventBus", topic = "topic", stage = Stage.AFTER)
        public void hi(String arg1, String arg2) {
        }
```


# Demo

```java
    
    public static final String[] oldUsers = new String[]{"Tom", "William", "Wilbert", "David", "Sofia"};
    public static final String[] newUsers = new String[]{"Louis", "Lily", "Taylor", "Jake", "Gavin"};

    @Test
    void test() {

        LocalEventBus localEventBus = new LocalEventBus(Executors.newFixedThreadPool(4));
        localEventBus.setSubscriberExceptionHandler(LoggedEventExceptionHandle.message());
        Subscriber subscriber = new Subscriber();
        localEventBus.register(subscriber);
        localEventBus.post("world", "newUser");
        for (String oldUser : oldUsers) {
            localEventBus.post(oldUser, "oldUser");
        }
        for (String newUser : newUsers) {
            localEventBus.post(newUser, "newUser");
        }
        localEventBus.post("Jeremiah");
        localEventBus.unregister(subscriber);
        localEventBus.post("Emily");
        Runs.uncheckDo(() -> Thread.sleep(TimeUnit.SECONDS.toMillis(10)));
    }

    private class Subscriber {

        Random random = new Random(System.currentTimeMillis());

        AtomicInteger i = new AtomicInteger(0);
        AtomicInteger newUserCount = new AtomicInteger(0);
        AtomicInteger oldUserCount = new AtomicInteger(0);

        @Subscribe
        private void listenString(String str, String topic) {
            System.out.println(MessageFormat.format("A String Event:[{0}],topic:[{1}]", str, topic));
        }

        @Subscribe(topic = "*User")
        private void listenUser(String name, String topic) {
            assert topic.endsWith("User");
            System.out.println(MessageFormat.format("[{0}]{1} {2} login", i.getAndIncrement(), topic, name));
        }

        @Retry(times = 3)
        @Subscribe(topic = "newUser", concurrency = 2)
        private void listenNewUser(String name) {
            if (random.nextBoolean()) {
                throw new IllegalArgumentException("error hello new user:" + name);
            }
            int count = newUserCount.incrementAndGet();
            assert count < 3;
            System.out.println(MessageFormat.format("[{0}]hello {1}， welcome here", count, name));
            Runs.uncheckDo(() -> Thread.sleep(TimeUnit.MILLISECONDS.toMillis(500)));
            newUserCount.decrementAndGet();
        }

        @Retry(times = 3)
        @Subscribe(topic = "oldUser", concurrency = 3)
        private void listenOldUser(String name) {
            if (random.nextBoolean()) {
                throw new IllegalArgumentException("error hello old user:" + name);
            }
            int count = oldUserCount.incrementAndGet();
            assert count < 4;
            System.out.println(MessageFormat.format("[{0}]hello {1}， welcome back", count, name));
            Runs.uncheckDo(() -> Thread.sleep(TimeUnit.MILLISECONDS.toMillis(500)));
            oldUserCount.decrementAndGet();
        }
    }
```