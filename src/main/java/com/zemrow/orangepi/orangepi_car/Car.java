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

    public static final int HELM_MIN = -50;
    public static final int HELM_MAX = 50;

    public static final int CAM_LEFT_RIGHT_MIN = -90;
    public static final int CAM_LEFT_RIGHT_MAX = 90;

    public static final int CAM_TOP_BUTTON_MIN = 0;
    public static final int CAM_TOP_BUTTON_MAX = 90;


    private final Motor leftMotor;
    private final Motor rightMotor;
    protected final Servo helm;
    protected final Servo camLeftRight;
    protected final Servo camTopButton;

    public Car(Motor leftMotor, Motor rightMotor, Servo helm, Servo camLeftRight, Servo camTopButton) {
        this.leftMotor = leftMotor;
        this.rightMotor = rightMotor;
        this.helm = helm;
        this.camLeftRight = camLeftRight;
        this.camTopButton = camTopButton;
    }

    public void read(Reader reader) throws Exception {
        final JSONObject json = new JSONObject(new JSONTokener(reader));
        setHelm(json.getInt(HELM));
        setSpeed(json.getInt(SPEED));
        setCamLeftRight(json.getInt(CAM_LEFT_RIGHT));
        setCamTopButton(json.getInt(CAM_TOP_BUTTON));
    }

    public void write(Writer writer) throws IOException {
        writer.write('{');
        writer.write(SPEED + ':' + leftMotor.getSpeed() + ',');
        writer.write(HELM + ':' + helm.getAngle() + ',');
        writer.write(CAM_LEFT_RIGHT + ':' + camLeftRight.getAngle() + ',');
        writer.write(CAM_TOP_BUTTON + ':' + camTopButton.getAngle());
        writer.write('}');
    }

    public void setSpeed(int speed) throws Exception {
        // при повороте руля скорость колеса двигающегося по внутринему радиусу уменьшаем
        if (helm.getAngle() > HELM_MAX / 2) {
            leftMotor.setSpeed(speed / 2);
        } else {
            leftMotor.setSpeed(speed);
        }
        if (helm.getAngle() < HELM_MIN / 2) {
            rightMotor.setSpeed(speed / 2);
        } else {
            rightMotor.setSpeed(speed);
        }
    }

    public void setHelm(int helm) throws Exception {
        this.helm.setAngle(helm);
    }

    public void setCamLeftRight(int angle) throws Exception {
        camLeftRight.setAngle(angle);
    }

    public void setCamTopButton(int angle) throws Exception {
        camTopButton.setAngle(angle);
    }
}

