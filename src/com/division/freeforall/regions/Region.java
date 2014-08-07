package com.division.freeforall.regions;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

public class Region {

	protected BlockVector min;
	protected BlockVector max;
	World world;

	public Region(World world, BlockVector p1, BlockVector p2) {
		List<Vector> points = new ArrayList<Vector>();
		points.add(p1);
		points.add(p2);
		setMinMaxPoints(points);
		this.world = world;
	}

	public boolean contains(World world, Vector pt) {
		if (!this.world.getName().equals(world.getName())) {
			return false;
		}

		final double x = pt.getX();
		final double y = pt.getY();
		final double z = pt.getZ();
		return x >= min.getBlockX() && x < max.getBlockX() + 1
				&& y >= min.getBlockY() && y < max.getBlockY() + 1
				&& z >= min.getBlockZ() && z < max.getBlockZ() + 1;
	}

	private void setMinMaxPoints(List<Vector> points) {
		int minX = points.get(0).getBlockX();
		int minY = points.get(0).getBlockY();
		int minZ = points.get(0).getBlockZ();
		int maxX = minX;
		int maxY = minY;
		int maxZ = minZ;

		for (Vector v : points) {
			int x = v.getBlockX();
			int y = v.getBlockY();
			int z = v.getBlockZ();

			if (x < minX) {
				minX = x;
			}
			if (y < minY) {
				minY = y;
			}
			if (z < minZ) {
				minZ = z;
			}

			if (x > maxX) {
				maxX = x;
			}
			if (y > maxY) {
				maxY = y;
			}
			if (z > maxZ) {
				maxZ = z;
			}
		}

		min = new BlockVector(minX, minY, minZ);
		max = new BlockVector(maxX, maxY, maxZ);
	}

	public void setRegion(BlockVector p1, BlockVector p2) {
		List<Vector> points = new ArrayList<Vector>();
		points.add(p1);
		points.add(p2);
		setMinMaxPoints(points);
	}
}
