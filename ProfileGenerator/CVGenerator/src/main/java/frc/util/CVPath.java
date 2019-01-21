package frc.util;

import java.util.Queue;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.drive.Vector2d;
import frc.robot.Robot;

public class CVPath extends Command {
    // How many points on MP path
    static final int PATH_RESOLUTION = 100;
    static final double R = 0.8;
    static final double MAX_VELOCITY = 0;
    static final double MAX_ACCELERATION = 0;
    static final double MAX_JERK = 0;
    // Angle to left and rightmost targets
    double alpha, beta = 0;
    // Distance from left and rightmost targets
    double d1, d2 = 0;
    double[] MyTrajectory;
    Vector2d farGoal, nearGoal,goalPoint;
    Vector2d h1, h2;// Two handle points
    double m_h2;// Slope of line which the 2nd header point lies on.

    public CVPath(double d1, double d2, double alpha, double beta) {
        requires(Robot.drivetrain);
        h1 = new Vector2d();
        h2 = new Vector2d();
        refreshData(d1, d2, alpha, beta);
    }

    public void refreshData(double d1, double d2, double alpha, double beta) {
        this.d1 = d1;
        this.d2 = d2;
        this.alpha = alpha;
        this.beta = beta;
        updatePoints();
        updateSlope();
        updateh1();
        updateh2();
    }

    private void updatePoints(){
        //get furthest Y point from the bot
        if(Math.abs(Math.sin(alpha)*d1) > Math.abs(Math.sin(beta)*d2){
            farGoal = new Vector2d(Math.cos(alpha)*d1, Math.sin(alpha)*d1);
            nearGoal = new Vector2d(Math.cos(beta)*d2, Math.sin(beta)*d2);
            return;
        }
        nearGoal = new Vector2d(Math.cos(alpha)*d1, Math.sin(alpha)*d1);
        farGoal = new Vector2d(Math.cos(beta)*d2, Math.sin(beta)*d2);
        goalPoint = new Vector2d((farGoal.x+nearGoal.x)/2.0, (farGoal.y+nearGoal.y)/2.0);
    }

    private void updateSlope() {//gets slopf of the line that the second header point lies on
        if (farGoal.y == nearGoal.y) {
            farGoal.y += Math.signum(farGoal.y) / (4096.0);
        }
        m_h2 = Math.abs((farGoal.x - nearGoal.x) / (farGoal.y - nearGoal.y));// (1/slope)
        if (farGoal.x < nearGoal.x) {
            m_h2 = -m_h2;
        }
    }

    private void updateh1() {
        h1.x = (farGoal.x + nearGoal.x) / 4.0;
    }
    private void updateh2(){
        h2.x = ((1.0-R)*Math.abs((nearGoal.x-farGoal.x)/(Math.abs(farGoal.x-nearGoal.x)+Math.abs(farGoal.y-nearGoal.y))) + R)*goalPoint.x;
        h2.y = m_h2*h2.x-(m_h2*goalPoint.x-goalPoint.y);
    }
    public Queue<double[]> getMyTrajectory() {
        // x,y,heading,position,velocity,acceleration
        //TODO: use Tahsin's Profiler to do this
        return null;
    }

    public boolean isFinished() {
        return false;
    }
}