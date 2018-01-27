package com.zemrow.orangepi.orangepi_car;

public abstract class Servo {
    /**
     * Наименование сервомотора
     */
    private final String name;
    /**
     * Ограничение минимального угол
     */
    private final int minAngle;
    /**
     * Ограничение на максимальный угол
     */
    private final int maxAngle;
    /**
     * Текущее значение угла
     */
    private int angle;

    public Servo(String name, int minAngle, int maxAngle) {
        this.name = name;
        this.minAngle = minAngle;
        this.maxAngle = maxAngle;
    }

    public int getAngle() {
        return angle;
    }

    /**
     * Установить угол
     *
     * @param angle угол
     * @return изменилось ли значение
     * @throws Exception выход за пределы minAngle, maxAngle
     */
    public boolean setAngle(int angle) throws Exception {
        boolean result = false;
        if (this.angle != angle) {
            if (angle < minAngle || angle > maxAngle) {
                throw new IllegalArgumentException(name + " angle " + angle + " not range " + minAngle + ".." + maxAngle);
            }
            this.angle = angle;
            result = true;
        }
        return result;
    }
}
