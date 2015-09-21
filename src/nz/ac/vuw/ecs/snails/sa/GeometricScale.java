package nz.ac.vuw.ecs.snails.sa;

public class GeometricScale implements Scale {
	private final double base;
	private final double maxRange;

	public GeometricScale(double maxDomain, double minRange, double maxRange) {
		this.maxRange = maxRange;
		base = Math.pow(minRange/maxRange,1/maxDomain);
	}

	@Override
	public double scale(int x) {
		return (maxRange * Math.pow(base, x));
	}

}
