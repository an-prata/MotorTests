// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.tests;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.DutyCycleEncoder;

/** 
 * A class for running a four bar mechanism in a thread so that it moves to a
 * given setpoint using a PID loop.
 */
public class FourBar implements Runnable {
    private static final int MOTOR_CAN_ID = 0;
    private static final int ENCODER_CHANNEL = 0;

    private static final long SCHEDULED_EXECUTOR_PERIOD = 10;
    private static final TimeUnit SCHEDULED_EXECUTOR_PERIOD_UNIT = TimeUnit.MILLISECONDS;

    private static final double FOUR_BAR_UPPER_BOUND = 1.0;
    private static final double FOUR_BAR_LOWER_BOUND = 0.0;

    private static final double PID_P = 0.0000001;
    private static final double PID_I = 0.0;
    private static final double PID_D = 0.0;
    
    private static final FourBar instance = new FourBar(MOTOR_CAN_ID, ENCODER_CHANNEL);

    private CANSparkMax motor;
    private DutyCycleEncoder encoder;
    private PIDController controller;
    private ScheduledExecutorService executorService;

    /**
     * Sets the four bar's setpoint.
     * 
     * @param position The position to set the bar to.
     * 
     * @return The set position, may be different from the given position if it
     *         exceeds the four bars' bounds.
     */
    public static double moveToPosition(double position) {
        if (position > FOUR_BAR_UPPER_BOUND)
            position = FOUR_BAR_UPPER_BOUND;
        
        if (position < FOUR_BAR_LOWER_BOUND)
            position = FOUR_BAR_LOWER_BOUND;
        
        instance.controller.setSetpoint(position);
        return position;
    }

    /**
     * Starts the four bar's PID loop thread.
     */
    public static void runThread() {
        instance.executorService = Executors.newSingleThreadScheduledExecutor();
        instance.executorService.scheduleAtFixedRate(instance, 0, SCHEDULED_EXECUTOR_PERIOD, SCHEDULED_EXECUTOR_PERIOD_UNIT);
    }

    /**
     * Stops the four bar's PID loop thread.
     */
    public static void stopThread() {
        if (instance.executorService.isShutdown() || instance.executorService.isTerminated())
            return;
        
        instance.executorService.shutdown();
    }

    @Override
    public void run() {
        double encoderPosition = encoder.get();
        double calculatedSpeed = controller.calculate(encoderPosition);
        motor.set(calculatedSpeed);
    }

    private FourBar(int motorID, int encoderChannel) {
        motor = new CANSparkMax(motorID, MotorType.kBrushless);
        encoder = new DutyCycleEncoder(encoderChannel);
        controller = new PIDController(PID_P, PID_I, PID_D);
    }
}
