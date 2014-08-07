package com.division.freeforall.regions;

public class Arena {

	private Region region;
	private HealRegion healregion;

	public Arena(Region region, HealRegion healRegion) {
		this.setRegion(region);
		this.setHealRegion(healRegion);
	}

	public HealRegion getHealRegion() {
		return healregion;
	}

	public Region getRegion() {
		return region;
	}

	public void setHealRegion(HealRegion healregion) {
		this.healregion = healregion;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	@Override
	public String toString() {
		return "Arena " + region.world.getName();
	}
}
