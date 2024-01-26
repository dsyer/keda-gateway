package com.example;

public interface ScalerService {

	boolean isActive(String name);

	void setActive(String name, boolean active);

	long getMetric(String name);

}
