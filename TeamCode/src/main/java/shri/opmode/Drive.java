package shri.opmode;

import static shri.config.util.FieldConstants.*;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import shri.config.runmodes.Teleop;
import shri.pedroPathing.follower.Follower;


@TeleOp(name="Drive", group="A")
public class Drive extends OpMode {

    private Teleop teleop;

    @Override
    public void init() {
        teleop = new Teleop(hardwareMap, telemetry, new Follower(hardwareMap), blueObservationParkPose, false, gamepad1, gamepad2);
        teleop.init();
    }

    @Override
    public void start() {
        teleop.start();
    }

    @Override
    public void loop() {
        teleop.update();
    }

}
