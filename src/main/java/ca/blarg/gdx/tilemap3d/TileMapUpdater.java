package ca.blarg.gdx.tilemap3d;

public class TileMapUpdater implements Runnable {
	final TileMap tileMap;

	boolean waitingForVboCreation;
	TileChunk chunkNeedingVboCreation;
	float progress;
	boolean isRunning;
	boolean needToStop;

	public TileMapUpdater(TileMap tileMap) {
		this.tileMap = tileMap;
	}

	public float currentProgress() {
		return progress;
	}

	public boolean isUpdating() {
		return isRunning;
	}

	public TileChunk getChunkNeedingVboCreation() {
		return chunkNeedingVboCreation;
	}

	public boolean isWaitingForVboCreation() {
		return waitingForVboCreation;
	}

	public synchronized void signalDoneVboCreation() {
		waitingForVboCreation = false;
	}

	public synchronized void signalStop() { needToStop = true; }

	@Override
	public void run() {
		isRunning = true;
		chunkNeedingVboCreation = null;
		needToStop = false;

		TileChunk[] chunks = tileMap.getChunks();

		for (int i = 0; (i < tileMap.chunks.length && !needToStop); ++i) {
			progress = (float)i / (float)(chunks.length - 1);

			TileChunk chunk = chunks[i];
			tileMap.vertexGenerator.generateVertices(chunk);
			chunkNeedingVboCreation = chunk;
			waitingForVboCreation = true;

			// wait for the render thread to take care of VBO creation and single to us that it's done
			while (waitingForVboCreation)
				Thread.yield();

			chunkNeedingVboCreation = null;
		}

		progress = 1.0f;
		isRunning = false;
	}
}
