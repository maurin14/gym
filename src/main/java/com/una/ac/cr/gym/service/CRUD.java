/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.una.ac.cr.gym.service;

import java.util.List;

/**
 *
 * @author alira
 */
public interface CRUD<T> {

    public void save(T t);

    public void delete(int id);

    public List<T> getAll();

    public T getById(int id);
}
