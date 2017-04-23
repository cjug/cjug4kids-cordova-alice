/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.lang.reflect.Field;
import  org.lgna.story.Color;
/**
 *
 * @author bpaulin
 */
public class ColorUtil {
    public static Color toColor(String color) throws NoSuchFieldException, IllegalAccessException
    {
        Field colorField = Color.class.getField(color.toUpperCase());
        return (Color)colorField.get(Color.BLACK);
    }
}
