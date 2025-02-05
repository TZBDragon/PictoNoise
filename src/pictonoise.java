import javax.imageio.ImageIO;
import javax.sound.midi.*;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
//Copyright 2025 TBooraem
public class pictonoise {

    public static void pictonoise(String path) throws IOException {
        //take in a photograph
        BufferedImage pic = null;
        Scanner readFile = new Scanner(new File(path));
        pic = ImageIO.read(new File(path));

        int height = pic.getHeight();
        int width = pic.getWidth();
        int[][][] imageData = new int[width][height][3];
        for(int i=0; i < width; i++){
            for(int y=0; y<height; y++){
                int color = pic.getRGB(i,y);
                imageData[i][y][0] = color & 0xff;
                imageData[i][y][1] = (color & 0xff00) >> 8;
                imageData[i][y][2] =(color & 0xff0000) >> 16;
            }
        }
        //convert photo to black and white
        //take avg rbg values of each pixel and replace them with that avg
        Color[][] imageDataAvg = new Color[width][height];
        int[][] imageDataAvgInt = new int[width][height];
        for(int i = 0; i < width;i++){
            for(int y=0; y< height; y++){
                int color = (imageData[i][y][0] + imageData[i][y][1] + imageData[i][y][2]) / 3;
                imageDataAvg[i][y] = new Color(color,color,color);
                imageDataAvgInt[i][y] = (imageData[i][y][0] + imageData[i][y][1] + imageData[i][y][2]) / 3;
            }
        }

        BufferedImage bwImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        for(int i = 0; i < width;i++){
            for(int y=0; y< height; y++){
                bwImage.setRGB(i,y,imageDataAvg[i][y].getRGB());
            }
        }
        String outImage = path + "b&w.JPG";
        File finImage = new File(outImage);
        try {
            ImageIO.write(bwImage, "jpg", finImage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //Buffer will hold the sample points before they are turned into bytes
        float[] buffer = new float[width*44100];
        //We need an audio format for when we write to audio input stream, in this case it is also important that it matches the .wav format
        AudioFormat af = new AudioFormat((float)44100, 16, 1, true, false );
        //We need to convert from px to hz so we divide out max hz by out height the 2 is there becasue sin(2piXT)
        double conversionFactor = (double) 22000 / height;
        //This is our amp values for actually showing what the picture will look like
        double[][] amp = new double[width][height];
        double[][] conversions = new double[width][height];
        System.out.println("Finding Amps");
        for(int i=0; i < width; i++){
            for(int y =0; y < height; y++){
                //Amplitudes are found by dividing the color value by our sample rate
                //I can't explain why I hope someone else can one day
                //I think becasue it may need the scalar to be based off of the samples rather than and absolute from 0-1
                amp[i][y] = (double) imageDataAvgInt[i][y] / 44100;
                /*
                amp[i] += (double) imageDataAvgInt[i][y] / 44100;
                 */
                conversions[i][y] = (height - y) * conversionFactor;
            }
        }
        System.out.println("Calculating samples");
        System.out.println(buffer.length + " samples to calculate");
        for(int sample = 0; sample < buffer.length; sample++){
            //Current time is equal to the sample divided by sample rate
            double time = (double) sample / 44100;
            //temp value
            float hold = 0;
            for(int i = 0; i < height;i++){
                /*
                This is where the bulk of the math occurs and is semi-complicated, I only half understand what is going on
                to create a sine wave you need a function y = A * SIN (2pi * t * x)
                A is amplitude
                t is time
                x is the frequency
                It should be possible to add phase, but I don't believe that is necessary for this to work
                our amplitude is found above, time is also found above, the frequency is just the height of the px times the conversion factor, we height - i so that the image appears right side up.
                adding all of these points to hold appears to be the same as adding the function together and thus it works
                 */
                hold += (float) ( (amp[(int)time][i]) * Math.sin(2*Math.PI * time * (conversions[(int)time][i])));
            }
            buffer[sample] = hold;
        }
        //Final data array
        final byte[] byteBuffer = new byte[buffer.length *2];
        int bufferindex = 0;
        System.out.println("Writing bytes");
        for(int i= 0; i < byteBuffer.length; i++){
            //x is equal to the next buffer value times the max two's complement value
            final int x = (int)(buffer[bufferindex++] * 32767.0);
            // .wav uses littleendian and thus we need to write the bytes backwards
            byteBuffer[i++] = (byte)x;
            byteBuffer[i] = (byte)(x >>> 8);
        }
        System.out.println("Writing File");
        //output file
        File out = new File(path + "Audio.wav");
        //bais is what will write our bytes to audio
        ByteArrayInputStream bais = new ByteArrayInputStream(byteBuffer);
        //load all data into ais with format
        AudioInputStream ais = new AudioInputStream(bais, af, buffer.length);
        //write file with data
        AudioSystem.write(ais, AudioFileFormat.Type.WAVE, out);
        //close to finish task - I think
        ais.close();
        System.out.println("Complete");
    }
}
