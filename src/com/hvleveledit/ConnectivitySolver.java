package com.hvleveledit;

import java.util.ArrayList;
import java.util.List;

import com.osreboot.ridhvl.map.HvlMap;

public class ConnectivitySolver {
	public static class MapCoord {
		public int x, y;

		public MapCoord(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + x;
			result = prime * result + y;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			MapCoord other = (MapCoord) obj;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}

	}

	private HvlMap map;

	private List<MapCoord> found;

	public ConnectivitySolver(HvlMap map) {
		super();
		this.map = map;
		found = new ArrayList<MapCoord>();
	}

	public List<MapCoord> getConnectedTiles(MapCoord tile, int layer) {
		found.add(tile);

		int origTile = map.getTile(layer, tile.x, tile.y);

		if (tile.x < map.getMapWidth() - 1 && map.getTile(layer, tile.x + 1, tile.y) == origTile) {
			if (!found.contains(new MapCoord(tile.x + 1, tile.y))) {
				found.add(new MapCoord(tile.x + 1, tile.y));
				List<MapCoord> nextfound = getConnectedTiles(new MapCoord(tile.x + 1, tile.y), layer);
				for (MapCoord c : nextfound) {
					if (!found.contains(c))
						found.add(c);
				}
			}
		}

		if (tile.x > 0 && map.getTile(layer, tile.x - 1, tile.y) == origTile) {
			if (!found.contains(new MapCoord(tile.x - 1, tile.y))) {
				found.add(new MapCoord(tile.x - 1, tile.y));
				List<MapCoord> nextfound = getConnectedTiles(new MapCoord(tile.x - 1, tile.y), layer);
				for (MapCoord c : nextfound) {
					if (!found.contains(c))
						found.add(c);
				}
			}
		}

		if (tile.y < map.getMapHeight() - 1 && map.getTile(layer, tile.x, tile.y + 1) == origTile) {
			if (!found.contains(new MapCoord(tile.x, tile.y + 1))) {
				found.add(new MapCoord(tile.x, tile.y + 1));
				List<MapCoord> nextfound = getConnectedTiles(new MapCoord(tile.x, tile.y + 1), layer);
				for (MapCoord c : nextfound) {
					if (!found.contains(c))
						found.add(c);
				}
			}
		}

		if (tile.y > 0 && map.getTile(layer, tile.x, tile.y - 1) == origTile) {
			if (!found.contains(new MapCoord(tile.x, tile.y - 1))) {
				found.add(new MapCoord(tile.x, tile.y - 1));
				List<MapCoord> nextfound = getConnectedTiles(new MapCoord(tile.x, tile.y - 1), layer);
				for (MapCoord c : nextfound) {
					if (!found.contains(c))
						found.add(c);
				}

			}
		}

		List<MapCoord> tr = new ArrayList<MapCoord>();
		
		for (MapCoord c : found) {
			if (!tr.contains(c))
				tr.add(c);
		}
		
		return tr;
	}
}
