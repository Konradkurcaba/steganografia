/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.steganografia;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import javax.swing.SwingWorker;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;
/**
 *
 * @author Konrad
 */
class image_save extends SwingWorker<Void,Object> {

    
    
    BufferedImage image;
    int char_count;
    String input_text;
    int bits_in_pixel;
    boolean start;
    boolean direction;
    image_save(BufferedImage image,int bits_in_pixel,int char_count,String text,boolean start,boolean direction)
    {
        this.image = image;
        
        StringBuilder builder = new StringBuilder();
        char control_letter = (char) Integer.parseInt("0101010101010101",2);
        builder.append(control_letter);
        builder.append(text);
        builder.append(control_letter); 
        input_text = builder.toString();
        this.char_count = char_count;
        this.bits_in_pixel = bits_in_pixel;
        this.start = start;
        this.direction = direction;
        
    }
    
    
    @Override
    protected Void doInBackground() throws Exception {
    int ile = input_text.length();
    if(input_text.length()>char_count)
    {
        message m = new message("Wiadomosc jest za dluga");
        m.setVisible(true);
        return null;
    }
    int x, y ; // position in image
    x = y = 0;
    
    if(start == false)
    {
        x = image.getWidth()-1;
        y = image.getHeight()-1;
    }
    int color_position = 0;
    int r,g,b;
    r = g = b = bits_in_pixel / 3;
    if ( bits_in_pixel % 3 > 0) r++;
    if ( bits_in_pixel % 3 > 1) g++;
    
     StringBuilder red_bits_builder = null;
     StringBuilder green_bits_builder = null;
     StringBuilder blue_bits_builder = null;
     
     int color_value = image.getRGB(x,y); // pobranie koloru w postaci binarnej
     Color color = new Color(color_value); 
     // rozbicie koloru na skladowe
     int red = color.getRed();
     int green = color.getGreen();
     int blue = color.getBlue();
    
     red_bits_builder = new StringBuilder(Binary_convert.int_to_string(red, 8)); // builder z wpisanym kolorem czerwonym w postaci bitowej
     green_bits_builder = new StringBuilder(Binary_convert.int_to_string(green, 8));
     blue_bits_builder = new StringBuilder(Binary_convert.int_to_string(blue, 8));
    for(int i = 0;i<input_text.length();i++) // for jedzie przez wszystkie litery tekstu do wpisania
    {
        String buffer = Integer.toBinaryString(input_text.charAt(i));
        StringBuilder builder = new StringBuilder();
        int how_many = 16 - buffer.length(); // odjecie liczby bitow od 16
        // nowe skladowe
       
        
        for(int j = 0;j<how_many;j++)
        {
            builder.append('0'); // dopisanie z przodu tylu 0, żeby kazdy ciag mial 16 bitow
        }
        
        builder.append(buffer); // dopisanie reszy bitów 
        String char_bits = builder.toString();
        int counter = 0;
        //tworzenie nowych skladowych
      
        while(counter < 16)
        {

            // zapisywanie pixel_position - pozycja gdzie należy teraz zapisywac
            if(color_position < r) // jesli pozycja gdzie trzeba zapisywac jest mniejsza niz ilosc bitow do zapisania w r to trzeba zapisywac w r
            {
                
                int red_position = (8 - r) + color_position; // na ktorej pozycji czerwonego trzeba teraz zapisywac
                for(int k = red_position;k<8;k++) // zapisywanie na czerwonym 
                {
                red_bits_builder.setCharAt(red_position++,char_bits.charAt(counter++)); // zapisanie na pozycji czerwonego bajtu na na ktory wskazuje counter
                color_position++;
                if (counter > 15) break;
                }
                 if (counter > 15) break;
            
                
            }
            if(color_position < g+r) // jesli pozycja jest mniejsza niz b+r to trzeba zapisywac w niebieskim
            {
                
                
                
                int green_position = (8 - g) + (color_position - r);
                for(int k = green_position;k<8;k++)
                {
                    green_bits_builder.setCharAt(green_position++, char_bits.charAt(counter++));
                    color_position++;
                    if (counter > 15) break;
                }
                 if (counter > 15) break;
            
            }
               // w niebieskim zawsze cos zapisujemy
               blue_bits_builder = new StringBuilder(Binary_convert.int_to_string(blue, 8));
               int blue_position = (8-b) + (color_position - (r+g));
               for(int k = blue_position;k<8;k++)
               {
                   blue_bits_builder.setCharAt(blue_position++, char_bits.charAt(counter++));
                   color_position++;
                   if (counter > 15) break;
                   
               }
               if( blue_position != 8 && counter > 15) break;
               // jesli jestesmy tutaj to oznacza ze bity na tym kolorze sie skonczyly
               color_position = 0;
               
               // stworzenie nowego koloru ze skladowych
               int new_red = Integer.parseInt(red_bits_builder.toString(),2);
               int new_green = Integer.parseInt(green_bits_builder.toString(),2);
               int new_blue = Integer.parseInt(blue_bits_builder.toString(),2);
               Color new_color = new Color(new_red,new_green,new_blue);
               // konwersja nowego koloru na inta 
               int new_color_int = new_color.getRGB();
               
               
               
               image.setRGB(x, y, new_color_int);
               
               
               
               
               if(start == true && direction == true)
               {
               
                   x++;
                   if(x == image.getWidth())
                   {
                       x = 0;
                       y++;
                   }
               }
               if(start == false && direction == true)
               {
                   x--;
                   if(x == -1)
                   {
                       x=image.getWidth()-1;
                       y--;
                   }
               }
               if(start == true && direction == false )
               {
                    y++; 
                    if(y == image.getHeight())
                    {
                        y=0;
                        x++;
                    }
               }
               if(start == false && direction == false)
               {
                    y--;
                    if(y == -1)
                    {
                        y=image.getHeight() -1;
                        x--;
                        
                    }
                            
               }
               color_value = image.getRGB(x,y); // pobranie koloru w postaci binarnej
               color = new Color(color_value); 
               // rozbicie koloru na skladowe
               red = color.getRed();
               green = color.getGreen();
               blue = color.getBlue();
               red_bits_builder = new StringBuilder(Binary_convert.int_to_string(red, 8)); // builder z wpisanym kolorem czerwonym w postaci bitowej
               green_bits_builder = new StringBuilder(Binary_convert.int_to_string(green, 8));
               blue_bits_builder = new StringBuilder(Binary_convert.int_to_string(blue, 8));
            
            
            
        }
   
        
        
        
    }
    if(color_position != 0)
    {
    // stworzenie nowego koloru ze skladowych   
    Color new_color = new Color(Integer.parseInt(red_bits_builder.toString(),2),Integer.parseInt(green_bits_builder.toString(),2),Integer.parseInt(blue_bits_builder.toString(),2));
    // konwersja nowego koloru na inta 
    int new_color_int = new_color.getRGB();
    image.setRGB(x, y, new_color_int);
    }
        JFileChooser fileChooser = new JFileChooser("");
    FileNameExtensionFilter filter = new FileNameExtensionFilter("BMP","BMP");
    fileChooser.setFileFilter(filter);
    if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
        File file = fileChooser.getSelectedFile();
        
        ImageIO.write(image, "png", file);
        int test = image.getRGB(0, 0);
        System.out.print(image.getColorModel().getRGB(image));
    }
    return null;  
    }
    
}
class image_read extends SwingWorker<Void,Object> {

    BufferedImage image;
    int bits_in_pixel = 0;
    int text_length = 0;
    JTextArea output_text_field;
    boolean start;
    boolean direction;
    
    image_read(BufferedImage image,int bits_in_pixel,JTextArea area,boolean start,boolean direction)
    {
        this.image = image;
        this.bits_in_pixel = bits_in_pixel;
        this.output_text_field = area;
        this.start = start;
        this.direction = direction;
        
    }
    
    
    
    @Override
    protected Void doInBackground() throws Exception {
        
    StringBuilder builder_text = new StringBuilder();
    StringBuilder builder_bits = new StringBuilder();
    char control_letter = (char) Integer.parseInt("0101010101010101",2);
    int x, y ; // position in image
    x = y = 0;
    if(start == false)
    {
        x = image.getWidth()-1;
        y = image.getHeight()-1;
    }
    int color_position = 0;
    int r,g,b;
    r = g = b = bits_in_pixel / 3;
    if ( bits_in_pixel % 3 > 0) r++;
    if ( bits_in_pixel % 3 > 1) g++;
    
     StringBuilder red_bits_builder = null;
     StringBuilder green_bits_builder = null;
     StringBuilder blue_bits_builder = null;
     
     int color_value = image.getRGB(x,y); // pobranie koloru w postaci binarnej
     Color color = new Color(color_value); 
     // rozbicie koloru na skladowe
     int red = color.getRed();
     int green = color.getGreen();
     int blue = color.getBlue();
    char new_letter = 0;
    int mark = 0;
    while(new_letter != control_letter) // for jedzie przez wszystkie litery tekstu do wpisania
    {
       
        int bits_counter = 0;
        
      
        while(bits_counter < 16)
        {
            
            // odczytywanie, pixel_position - pozycja gdzie należy teraz odczytywać
            if(color_position < r) // jesli pozycja gdzie trzeba zapisywac jest mniejsza niz ilosc bitow do czytania w r to trzeba czytac w r
            {
                red_bits_builder = new StringBuilder(Binary_convert.int_to_string(red, 8)); // builder z wpisanym kolorem czerwonym w postaci bitowej
                int red_position = (8 - r) + color_position; // na ktorej pozycji czerwonego trzeba teraz czytac
                for(int k = red_position;k<8;k++) // czytanie na czerwonym 
                {
                builder_bits.append(red_bits_builder.charAt(k));
                bits_counter++;
                color_position++;
                if (bits_counter > 15) break;
                }
                 if (bits_counter > 15) break;
            
                
            }
            if(color_position < g+r) // jesli pozycja jest mniejsza niz b+r to trzeba czytac w niebieskim
            {
                
                
                green_bits_builder = new StringBuilder(Binary_convert.int_to_string(green, 8));
                int green_position = (8 - g) + (color_position - r);
                for(int k = green_position;k<8;k++)
                {
                    
                    builder_bits.append(green_bits_builder.charAt(k));
                    color_position++;
                    bits_counter++;
                    if (bits_counter > 15) break;
                }
                 if (bits_counter > 15) break;
            
            }
               // w niebieskim zawsze cos czytamy
               blue_bits_builder = new StringBuilder(Binary_convert.int_to_string(blue, 8));
               int blue_position = (8-b) + (color_position - (r+g));
               for(int k = blue_position;k<8;k++)
               {
                   builder_bits.append(blue_bits_builder.charAt(k));
                   bits_counter++;
                   color_position++;
                   if (bits_counter > 15) break;
                   
               }
               if( blue_position != 8 && bits_counter > 15) break;
               // jesli jestesmy tutaj to oznacza ze bity na tym kolorze sie skonczyly
               color_position = 0;
                if(start == true && direction == true)
               {
               
                   x++;
                   if(x == image.getWidth())
                   {
                       x = 0;
                       y++;
                   }
               }
               if(start == false && direction == true)
               {
                   x--;
                   if(x == -1)
                   {
                       x=image.getWidth()-1;
                       y--;
                   }
               }
               if(start == true && direction == false )
               {
                    y++; 
                    if(y == image.getHeight())
                    {
                        y=0;
                        x++;
                    }
               }
               if(start == false && direction == false)
               {
                    y--;
                    if(y == -1)
                    {
                        y=image.getHeight() -1;
                        x--;
                        
                    }
                            
               }
               color_value = image.getRGB(x,y); // pobranie koloru w postaci binarnej
               color = new Color(color_value); 
               // rozbicie koloru na skladowe
               red = color.getRed();
               green = color.getGreen();
               blue = color.getBlue();
                
                
                
            }
            if (mark == 0)
            {
                new_letter = (char) Integer.parseInt(builder_bits.toString(),2);
                if (new_letter != control_letter) 
                {
                    // errora
                    break;
                }
                new_letter = 0;
                mark = 1;
            }
            else
            {
                new_letter = (char) Integer.parseInt(builder_bits.toString(),2); // skladanie litery
                builder_text.append(new_letter); // dopisanie jej do tekstu
                builder_bits.setLength(0); // zmiana buildera bitowego na 0;
            }
            
            
        }
        if(builder_text.length() != 0)builder_text.setLength(builder_text.length() - 1);
        publish(builder_text.toString());
        
        
        
        return null;
        
    }
    
    
    protected void process(List<Object> chunks)
    {
        String tekst = (String)chunks.get(0);
        if(tekst.length() == 0)  output_text_field.setText("W tym obrazie nie ma ukrytej wiadomosci");
        else output_text_field.setText(tekst);
        
        
        
        
    }
    
    
    
    
    
}