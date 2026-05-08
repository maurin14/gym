/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.una.ac.cr.gym.service;

import java.util.List;

/**
 *
 * @author PC
 */
public interface CRUD <T>{
  
    public void save(T t);
    public void delete(int id);
    public List<T> getAll();
    public T getByid(int id);
    public void update(int id, T t);

}
