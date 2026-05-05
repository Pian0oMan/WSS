public class Path {
    private int[] position;
    private int[] costs;

    public Path(int[] position, int[] costs) {
        this.position = position;
        this.costs = costs;
    }

    public int[] getPosition() { return position; }
    public int[] getCosts() { return costs; }
}