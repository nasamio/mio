package com.mio.base;

public class ConcreteFactory {
    private ConcreteFactory() {
    }

    public static ConcreteFactory getInstance() {
        return ConcreteFactoryHolder.instance;
    }

    private final static class ConcreteFactoryHolder {
        final static ConcreteFactory instance = new ConcreteFactory();
    }

    /**
     * 反射获取产品
     */
    public <T extends Car> T createCar(Class<T> clazz) {
        Class cls = null;
        try {
            cls = Class.forName(clazz.getCanonicalName());
            return (T) cls.newInstance();
        } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}

class Car {

}