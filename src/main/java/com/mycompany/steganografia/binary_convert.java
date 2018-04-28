/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.steganografia;

/**
 *
 * @author Konrad
 */
class Binary_convert {
    
    
    static String int_to_string(int x,int bit_count)
    {
        String buffer = Integer.toBinaryString(x);
        StringBuilder builder = new StringBuilder();
        int how_many = bit_count - buffer.length(); // odjecie liczby bitow od 16
        for(int j = 0;j<how_many;j++)
        {
            builder.append('0'); // dopisanie z przodu tylu 0, żeby kazdy ciag mial 16 bitow
        }
        
        builder.append(buffer); // dopisanie reszy bitów 
        return builder.toString();
        
    }
    
    
    
    
}
