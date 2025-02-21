package shri.opmode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import shri.config.subsystem.ExtendSubsystem;
import shri.config.subsystem.IntakeSubsystem;
import shri.config.subsystem.VisionSubsystem;

@TeleOp (name = "visiontest")
public class VisionTest extends OpMode {
    VisionSubsystem vision;
    ExtendSubsystem extend;
    IntakeSubsystem intake;
    IntakeSubsystem.IntakePivotState intakePivotState;
    IntakeSubsystem.IntakeSpinState intakeSpinState;
    boolean pressed = false;


    @Override
    public void init() {
        extend = new ExtendSubsystem(hardwareMap, telemetry);
        vision = new VisionSubsystem(hardwareMap, telemetry, extend);
        intake = new IntakeSubsystem(hardwareMap, intakeSpinState, intakePivotState);
        vision.switchPipeline(VisionSubsystem.limelightState.blue);
        extend.toQuarter();
    }

    @Override
    public void start() {
        vision.start();
        intake.pivotGround();
    }

    @Override
    public void loop() {
        vision.updateColor();

        if(gamepad1.a && pressed == false) {
            vision.extendAlign(vision.getTxError());
            vision.driveAlign(vision.getTyError());
            pressed = true;
        }

        if(gamepad1.dpad_down) {
            extend.toQuarter();
            pressed = false;
        }

        if(gamepad1.b) {
            intake.spinIn();
        } else if(gamepad1.x) {
            intake.spinOut();
        } else if (gamepad1.y) {
            intake.spinStop();
        }
    }
}
