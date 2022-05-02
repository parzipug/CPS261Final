package application;

import java.time.LocalDate;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Player {
	private final StringProperty name = new SimpleStringProperty();
	private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<LocalDate>();
	private final StringProperty result = new SimpleStringProperty();
	private final IntegerProperty points = new SimpleIntegerProperty();

	public Player(String name, LocalDate date) {
		setName(name);
		setDate(date);
	}
	
	public Player(String name, LocalDate date, String result, int points) {
		setName(name);
		setDate(date);
		setResult(result);
		setPoints(points);
	}
	
	public final void setName(String name) {this.name.set(name);}
	public final void setDate(LocalDate date) {this.date.set(date);}
	public final void setResult(String result) {this.result.set(result);}
	public final void setPoints(int points) {this.points.set(points);}
	public final void addPoints(int points) {this.points.set(this.getPoints() + points);}
	
	public final String getName() {return name.get();}
	public final LocalDate getDate() {return date.get();}
	public final String getResult() {return result.get();}
	public final int getPoints() {return points.get();}
	
	public final StringProperty nameProperty() {return name;}
	public final ObjectProperty<LocalDate> dateProperty() {return date;}
	public final StringProperty resultProperty() {return result;}
	public final IntegerProperty pointsProperty() {return points;}
}
