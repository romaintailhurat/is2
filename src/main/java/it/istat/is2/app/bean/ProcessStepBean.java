/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.istat.is2.app.bean;

import java.io.Serializable;
import lombok.Data;

/**
 *
 * @author mbruno
 */
@Data
public class ProcessStepBean implements Serializable{
    
    private static final long serialVersionUID = -5161466911184225333L;
    
    private Long id;
    private String name;

    public ProcessStepBean(Long id, String name) {
        this.id = id;
        this.name = name;
    }
    
    
    
}
