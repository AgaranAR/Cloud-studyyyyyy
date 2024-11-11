package com.example.clo;

public class Course {
    private String name;

    public Course(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    // Override equals and hashCode for proper comparison in ArrayList
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Course course = (Course) obj;
        return name.equals(course.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
