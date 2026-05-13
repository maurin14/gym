package com.una.ac.cr.gym.service;

/**
 *
 * @author Amanda
 */

import com.una.ac.cr.gym.domain.GymClass;
import com.una.ac.cr.gym.repository.GymClassRepository;
import java.time.Duration;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class GymClassService {

    private final GymClassRepository gymClassRepository;

    public GymClassService(GymClassRepository gymClassRepository) {
        this.gymClassRepository = gymClassRepository;
    }

    public List<GymClass> getAllClasses() {
        return gymClassRepository.findAll();
    }

    public Page<GymClass> getClassesPage(int page, int size) {
        return gymClassRepository.findAll(PageRequest.of(page, size));
    }

    public GymClass getClassById(int idClass) {
        return gymClassRepository.findById(idClass).orElse(null);
    }

    public GymClass saveClass(GymClass gymClass) {

        calculateDuration(gymClass);

        gymClass.setStatus(true);

        return gymClassRepository.save(gymClass);
    }

    public GymClass updateClass(int idClass, GymClass gymClass) {

        gymClass.setIdClass(idClass);

        calculateDuration(gymClass);

        return gymClassRepository.save(gymClass);
    }

    public void deleteClass(int idClass) {
        gymClassRepository.deleteById(idClass);
    }

    private void calculateDuration(GymClass gymClass) {

        int duration = (int) Duration.between(
                gymClass.getStartTime(),
                gymClass.getEndTime()
        ).toMinutes();

        gymClass.setDuration(duration);
    }
}