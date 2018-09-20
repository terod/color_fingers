package com.reice.misc.ColorFingers;

/**
 * Write a description of class AngleCalc here.
 * 
 * @author George Ebbinason
 * @version v1
 */
public class ColorCalc
{
    // instance variables - replace the example below with your own
    private MPoint p1;
    private MPoint p2;

    /**
     * Constructor for objects of class AngleCalc
     */
    public ColorCalc(MPoint p1, MPoint p2)
    {
        // initialise instance variables
        this.p1 = p1;
        this.p2 = p2;
    }

    /**
     * An example of a method - replace this comment with your own
     * 
     * @param  y   a sample parameter for a method
     * @return     the sum of x and y 
     */
    public double calculateAngle()
    { 
        double dx,dy;
        // put your code here
        dx = p2.getX() - p1.getX();
        dy = p2.getY() - p1.getY();
        double angle = Math.toDegrees(Math.atan2(dy,dx));
        if(angle < 0)
            angle += 360;
        return angle;
    }
    
    public double calculateAngleNormalized()
    { 
        double dx,dy;
        // put your code here
        dx = p2.getX() - p1.getX();
        dy = p2.getY() - p1.getY();
        double angle = Math.toDegrees(Math.atan2(dy,dx));
        if(angle < 0)
            angle += 360;
        return angle/360;
    }
    
    public double calculateDistance()
    {
        return p1.distance(p2);
    }
    
    public double calculateDistanceNormalized()
    {
        return p1.distance(p2)/TouchActivity.padding;
    }
    
    
    public int[] calculateHSVtoRGB(double H, double S, double V)
    {
        double var_i, var_h, var_1, var_2, var_3;
        double var_r, var_g, var_b;
        double R, G, B;
        int RGB[] = new int[3];
        if ( S == 0 )                       //HSV from 0 to 1
        {
           R = V * 255;
           G = V * 255;
           B = V * 255;
        }
        else
        {

           var_h = H * 6;
           if ( var_h == 6 ) var_h = 0 ;      //H must be < 1
           var_i = (int) var_h ;             //Or ... var_i = floor( var_h )
           var_1 = V * ( 1 - S );
           var_2 = V * ( 1 - S * ( var_h - var_i ) );
           var_3 = V * ( 1 - S * ( 1 - ( var_h - var_i ) ) );
        
           if      ( var_i == 0 ) { var_r = V     ; var_g = var_3 ; var_b = var_1; }
           else if ( var_i == 1 ) { var_r = var_2 ; var_g = V     ; var_b = var_1; }
           else if ( var_i == 2 ) { var_r = var_1 ; var_g = V     ; var_b = var_3; }
           else if ( var_i == 3 ) { var_r = var_1 ; var_g = var_2 ; var_b = V;     }
           else if ( var_i == 4 ) { var_r = var_3 ; var_g = var_1 ; var_b = V;     }
           else                   { var_r = V     ; var_g = var_1 ; var_b = var_2; }
        
           R = var_r * 255;                  //RGB results from 0 to 255
           G = var_g * 255;
           B = var_b * 255;
        }
        RGB[0] = (int) R;
        RGB[1] = (int) G;
        RGB[2] = (int) B;
        return RGB;
    }
}
