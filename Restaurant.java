import java.util.*;
import java.util.stream.Collectors;

class Table implements Comparable<Table> {
	public final int size;
	private int freeSeats;

	public Table(int size) {
		this.size = size;
		this.setFreeSeats(size);
	}

	@Override
	public int compareTo(Table table) {
		return table.getFreeSeats() - this.getFreeSeats();
	}

	public int getFreeSeats() {
		return freeSeats;
	}

	public void setFreeSeats(int freeSeats) {
		this.freeSeats = freeSeats;
	}
}

class CustomerGroup {
	public final int size;
	private Table table;

	public CustomerGroup(int size) {
		this.size = size;
	}

	public Table getTable() {
		return table;
	}

	public void setTable(Table table) {
		this.table = table;
	}
}

class SeatingManager {
	private List<Table> tables;
	private List<CustomerGroup> waitingQueue = new ArrayList<>();

	// Running time of constructing manager is O(n*log(n)).
	// Memory consumption in worst case is n/2 for applying sorting list of tables
	SeatingManager(List<Table> tables) {
		this.tables = tables;
		Collections.sort(tables);
	}

	// Running time of placing group at the table is approximately n since it is nearly sorted,
	// memory consumption in worst case is n/2 for applying sorting list of tables if group was placed at the table.
	// Running time of placing group to waiting queue is O(1) or O(n) if size of underlying array should be increased.
	void arrives(CustomerGroup group) {
		Table leastOccupiedTable = tables.get(0);
		if (leastOccupiedTable.getFreeSeats() >= group.size) {
			group.setTable(leastOccupiedTable);
			leastOccupiedTable.setFreeSeats(leastOccupiedTable.getFreeSeats() - group.size);
			Collections.sort(tables);
		} else {
			waitingQueue.add(group);
		}
	}

	// Running time is  O(1) if waiting queue is empty or O(n) if waiting queue needs to be checked where n = number of waiting groups.
	// Memory consumption in worst case is n/2 for applying sorting list of tables where n = number of tables.
	void leaves(CustomerGroup group) {
		Table table = group.getTable();
		table.setFreeSeats(table.getFreeSeats() + group.size);
		group.setTable(null);

		if (!waitingQueue.isEmpty()) {
			Iterator<CustomerGroup> iterator = waitingQueue.iterator();
			while (iterator.hasNext()) {
				CustomerGroup waitingGroup = iterator.next();
				if (table.getFreeSeats() >= waitingGroup.size) {
					waitingGroup.setTable(table);
					table.setFreeSeats(table.getFreeSeats() - waitingGroup.size);
					iterator.remove();
				}
			}
		}

		Collections.sort(tables);
	}

	// Running time of locating group's table is O(1), no additional memory consumption.
	Table locate(CustomerGroup group) {
		return group.getTable();
	}
}

public class Restaurant {
	public static void main(String[] args) {
		int numTables = 10;

		SeatingManager seatingManager = new SeatingManager(
			new Random()
				.ints(numTables, 2, 7)
				.mapToObj(Table::new)
				.collect(Collectors.toList())
		);

		int numCustomerGroups = 7;

		List<CustomerGroup> groupList = new Random()
			.ints(numCustomerGroups, 2, 7)
			.mapToObj(CustomerGroup::new)
			.collect(Collectors.toList());

		groupList.forEach(seatingManager::arrives);

		groupList.stream()
			.filter(g -> g.getTable() != null)
			.findAny()
			.ifPresent(seatingManager::leaves);

		seatingManager.locate(groupList.get(1));
	}
}
