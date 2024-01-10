package frc.robot.tests;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Motor test for most motors, adjust constants before hand.
 */
public class GenericMotorTest {
    private static final boolean IS_TALON_MOTOR = true;
    private static final int CONTROLLER_PORT = 0;
    private static final int MOTOR_CAN_ID = 6;
    
    private static XboxController xboxController;
    private static TalonSRX talon;
    private static CANSparkMax canSparkMax;

    /**
     * Run in robotInit.
     */
    public static void init() {
        // init motors based on MOTOR_CAN_ID constant
        if (IS_TALON_MOTOR)
            talon = new TalonSRX(MOTOR_CAN_ID);
        else
            canSparkMax = new CANSparkMax(MOTOR_CAN_ID, MotorType.kBrushless);

        // init controller (man, having a motor controller AND xbox controller
        // is hella confusing).
        xboxController = new XboxController(CONTROLLER_PORT);
    }

    /**
     * Run in teleopPeriodic.
     */
    public static void periodic() {
        double setSpeed = xboxController.getLeftY();

        setSpeed = Math.signum(setSpeed) * (Math.abs(setSpeed) > 0.8 ? 0.8 : Math.abs(setSpeed));
  
        if (IS_TALON_MOTOR) {
            talon.set(TalonSRXControlMode.PercentOutput, setSpeed);
        } else {
            canSparkMax.set(setSpeed);
            RelativeEncoder encoder = canSparkMax.getEncoder();
            SmartDashboard.putNumber("CANSparkMax Encoder Position", encoder.getPosition());
        }
    }
}
