/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.darkprograms.speech.synthesiser;

/**
 *
 * @author eduardo
 */
public class Updater extends Thread {
    private long id;
    private String text;
    
    public Updater(long id, String text) {
        this.id = id;
        this.text = text;
    }
    
    @Override
    public void run() {
        
    }
}
