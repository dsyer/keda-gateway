package com.example;

import org.springframework.stereotype.Component;

@Component
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

}
