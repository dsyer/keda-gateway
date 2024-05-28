package com.example;

public class SimpleScalerService implements ScalerService {

	private boolean active = true;

	@Override
	public boolean isActive(String name) {
		return active;
	}

	@Override
	public void setActive(String name, boolean active) {
		this.active = active;
	}

	@Override
	public long getMetric(String name) {
		return active ? 1 : 0;
	}

}
