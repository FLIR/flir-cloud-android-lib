package com.flir.sdk;


import io.reactivex.observers.DisposableObserver;

/**
 * Created by mamar on 11/29/2017.
 */

public class BaseDisposableObserverUnitTest<T> extends DisposableObserver {

    public BaseDisposableObserverUnitTest() {
    }

    @Override
    public void onNext(Object o) {

    }

    @Override
    public void onError(Throwable e) {
        UnitTestUtils.printOnErrorTestLog(e);
    }

    @Override
    public void onComplete() {

    }


}
