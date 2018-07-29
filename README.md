# RetrofitError
Simple http service error handler, to be able get error as HttpException, IOException
or something different, convert to human readable texts those given from user
 and also handle session logout states.

## Prerequisites
First, dependency must be added to build.gradle file.
```groovy
implementation 'nurisezgin.com.retrofiterror:retrofiterror:1.0.0'
```

## How To Use
* Use with Rx2Java, change **Rx2** call adapter factory with **Rx2RetrofitErrorCallAdapterFactory**
```java
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(webServer.url("/"))
            .addCallAdapterFactory(Rx2RetrofitErrorCallAdapterFactory.create())
            .build();
```
* Set customized actions to **RetrofitErrorPlugin**. There are session
logout action, error converter and error adapter. (If you don't put any of them
default methods will be run) Default error converter and session logout
action are empty, they won't any side effect on application but **DefaultErrorAdapter**
covers something, it handles an exception and triggers conversion actions over **ErrorConverter**
and it can call specific **SessionLogoutAction** or default session logout action.

```java
    RetrofitErrorPlugin.setDefaultSessionLogoutAction(action);
    RetrofitErrorPlugin.setErrorConverter(errorConverter);
    RetrofitErrorPlugin.setErrorAdapter(errorAdapter);
```

* As service interface, in below example method of **getUserFees** using default
 session action but other methods use customized actions.
```java
    interface Service {

        @DoOnSessionLogout(CustomAction.class)
        @GET("/balance")
        Observable<String> getUserBalance();

        @DoOnSessionLogout(CustomAction.class)
        @GET("/rawbalance")
        Observable<String> getRawUserBalance();

        @GET("/fees")
        Observable<String> getUserFees();

    }
```

* Must use empty constructor in which class has been implemented in SessionLogoutAction
```java
    public static class SimpleAction implements SessionLogoutAction {

        @Override
        public void onSessionLoggedOut(Request request) { }
    }
```

## Authors
* **Nuri SEZGIN**-[Email](acnnurisezgin@gmail.com)

## Licence
Copyright 2018 Nuri SEZGIN

Apache License 2.0

