package frc.robot.tests;

import frc.robot.*;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.*;

public class Claw {

    private DoubleSolenoid clawSolenoid;

    private static final int FORWARD_CHANNEL = 4;
    private static final int REVERSE_CHANNEL = 5;

    private boolean pneumaticsTransitioning;
    private boolean clawClosed;

    private static Claw instance = new Claw();

    private Claw() {

        pneumaticsTransitioning = false;
        clawClosed = false;

        clawSolenoid = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, FORWARD_CHANNEL, REVERSE_CHANNEL);
    }

    public static void run() {
        if(DriveJoystick.claw() && !instance.pneumaticsTransitioning) {
            instance.pneumaticsTransitioning = true;

            if(instance.clawClosed) {
                instance.clawSolenoid.set(Value.kReverse);
                instance.clawClosed = false;
            } else {
                instance.clawSolenoid.set(Value.kForward);
                instance.clawClosed = true;
            }
        }

        if(!DriveJoystick.claw()) 
            instance.pneumaticsTransitioning = false;
    }

    public static void close() {
        if(instance.clawSolenoid.get() == Value.kForward) {
            instance.clawSolenoid.set(Value.kReverse);
            instance.clawClosed = false;
        }
    }

    public static void release() {
        if(instance.clawSolenoid.get() == Value.kReverse) {
            instance.clawSolenoid.set(Value.kForward);
            instance.clawClosed = true;
        }
    }
}