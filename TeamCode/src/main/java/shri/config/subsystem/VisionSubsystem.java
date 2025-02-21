package shri.config.subsystem;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@Config
public class VisionSubsystem {

    private double tx, ty, ta;

    public enum limelightState {
        yellow,
        red,
        blue,
        aprilTag,
        none
    }

    private Telemetry telemetry;
    private ExtendSubsystem extend;

    public limelightState state;
    private Limelight3A limelight;
    private LLResult result;

    private int pipeline = 0;
    private double x = 0;
    private double y = 0;

    public static double extendMultipler = 0.0045;

    private DcMotor lf,rf,lb,rb;


    public VisionSubsystem(HardwareMap hardwareMap, Telemetry telemetry, ExtendSubsystem extend) {
        this.telemetry = telemetry;
        this.extend = extend;
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(100); // per sec

        lf = hardwareMap.get(DcMotor.class, "leftFront");
        rf = hardwareMap.get(DcMotor.class, "rightFront");
        lb = hardwareMap.get(DcMotor.class, "leftRear");
        rb = hardwareMap.get(DcMotor.class, "rightRear");
    }

    public void start() {
        limelight.start();
        limelight.pipelineSwitch(pipeline);
    }

    public void switchPipeline(limelightState state) {
        switch (state) {
            case yellow:
                pipeline = 0;
                break;
            case red:
                pipeline = 1;
                break;
            case blue:
                pipeline = 2;
                break;
            case aprilTag:
                pipeline = 3;
                break;
        }

        limelight.pipelineSwitch(pipeline);

        if (state == limelightState.none) {
            limelight.stop();
        }
    }




    public void updateColor() {
        update();
        if (result != null) {
            telemetry.addData("tx", result.getTx());
            telemetry.addData("ty", result.getTy());
        }
    }

    public void extendAlign(double error) {
        if(error > -25) {
            extend.setLimitToSample();
            double extendDistance = -(-25-error) * extendMultipler;

            if(extend.getPos() + extendDistance > extend.extendLimit)
                extend.setTarget(extend.extendLimit);
            else
                extend.setTarget(extend.getPos() + extendDistance);


        }


    }

    public void driveAlign(double error) {


    }

    public void update() {
        result = limelight.getLatestResult();

        if (result != null && result.isValid()) {
            tx = result.getTx(); // How far left or right the target is (degrees)
            ty = result.getTy(); // How far up or down the target is (degrees)
            ta = result.getTa(); // How big the target looks (0%-100% of the image)

            telemetry.addData("Target X", tx);
            telemetry.addData("Target Y", ty);
            telemetry.addData("Target Area", ta);
        } else {
            telemetry.addData("Limelight", "No Targets");
        }

        telemetry.update();
    }

    public double getTxError() {
        update();
        return result.getTx();
    }

    public double getTyError() {
        update();
        return result.getTy();
    }

    public void strafeLeft(double left) {
        double leftFrontPower = -left;
        double rightFrontPower = left;
        double leftBackPower = left;
        double rightBackPower = -left;

        // Normalize wheel powers to be less than 1.0
        double max = Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower));
        max = Math.max(max, Math.abs(leftBackPower));
        max = Math.max(max, Math.abs(rightBackPower));

        if (max > 1.0) {
            leftFrontPower /= max;
            rightFrontPower /= max;
            leftBackPower /= max;
            rightBackPower /= max;
        }

        // Send powers to the wheels.
        lf.setPower(leftFrontPower);
        rf.setPower(rightFrontPower);
        lb.setPower(leftBackPower);
        rb.setPower(rightBackPower);
    }

}