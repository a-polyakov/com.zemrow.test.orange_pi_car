package com.zemrow.orangepi.orangepi_car;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class Car {

    public static final String SPEED = "speed";
    public static final String HELM = "helm";
    public static final String CAM_LEFT_RIGHT = "camLeftRight";
    public static final String CAM_TOP_BUTTON = "camTopButton";

    private final Motor leftMotor;
    private final Motor rightMotor;
    private final Servo helm;
    private final Servo camLeftRight;
    private final Servo camTopButton;

    public Car(Motor leftMotor, Motor rightMotor, Servo helm, Servo camLeftRight, Servo camTopButton) {
        this.leftMotor = leftMotor;
        this.rightMotor = rightMotor;
        this.helm = helm;
        this.camLeftRight = camLeftRight;
        this.camTopButton = camTopButton;
    }

    public void read(Reader reader) throws Exception {
        final JSONObject json = new JSONObject(new JSONTokener(reader));
        setSpeed(json.getInt(SPEED));
        setHelm(json.getInt(HELM));
        setCamLeftRight(json.getInt(CAM_LEFT_RIGHT));
        setCamTopButton(json.getInt(CAM_TOP_BUTTON));
    }

    public void write(Writer writer) throws IOException {
        writer.write('{');
        writer.write(SPEED + ':' + leftMotor.getSpeed());
        writer.write(HELM + ':' + helm.getValue());
        writer.write(CAM_LEFT_RIGHT + ':' + camLeftRight.getValue());
        writer.write(CAM_TOP_BUTTON + ':' + camTopButton.getValue());
        writer.write('}');
    }

    public void setSpeed(int speed) throws Exception {
        leftMotor.setSpeed(speed);
        rightMotor.setSpeed(speed);
    }

    public void setHelm(int helm) throws Exception {
        if (helm < -50 || helm > 50) {
            throw new IllegalArgumentException("Helm angle " + helm + " not range -50..50");
        }
        this.helm.setValue(450 + helm * 500 / 180);
    }

    public void setCamLeftRight(int angle) throws Exception {
        if (angle < -90 || angle > 90) {
            throw new IllegalArgumentException("CamLeftRight angle " + angle + " not range -90..90");
        }
        camLeftRight.setValue(450 + angle * 500 / 180);
    }

    public void setCamTopButton(int angle) throws Exception {
        if (angle < 0 || angle > 90) {
            throw new IllegalArgumentException("CamTopButton angle " + angle + " not range 0..90");
        }
        camTopButton.setValue(200 + angle * 200 / 90);
    }
}

