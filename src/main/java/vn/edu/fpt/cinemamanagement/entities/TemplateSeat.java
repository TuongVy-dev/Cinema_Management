package vn.edu.fpt.cinemamanagement.entities;

import jakarta.persistence.*;
import vn.edu.fpt.cinemamanagement.converters.SeatTypeConverter;
import vn.edu.fpt.cinemamanagement.enums.SeatType;

import java.util.List;

@Entity
@Table(name = "template_seat")
public class TemplateSeat {
    @Id
    @Column(name = "template_seat_id")
    private String id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", referencedColumnName = "template_id")
    private Template template;
    @Column(name = "row_label")
    private String rowLabel;
    @Column(name = "seat_number")
    private int seatNumber;
    @Convert(converter = SeatTypeConverter.class)
    @Column(name = "seat_type")
    private SeatType seatType;

    @OneToMany(mappedBy = "templateSeat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShowtimeSeat> showtimeSeats;

    public TemplateSeat() {
    }

    public TemplateSeat(String id, Template template, String rowLabel, int seatNumber, SeatType seatType, List<ShowtimeSeat> showtimeSeats) {
        this.id = id;
        this.template = template;
        this.rowLabel = rowLabel;
        this.seatNumber = seatNumber;
        this.seatType = seatType;
        this.showtimeSeats = showtimeSeats;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

    public String getRowLabel() {
        return rowLabel;
    }

    public void setRowLabel(String rowLabel) {
        this.rowLabel = rowLabel;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }

    public SeatType getSeatType() {
        return seatType;
    }

    public void setSeatType(SeatType seatType) {
        this.seatType = seatType;
    }

    public List<ShowtimeSeat> getShowtimeSeats() {
        return showtimeSeats;
    }

    public void setShowtimeSeats(List<ShowtimeSeat> showtimeSeats) {
        this.showtimeSeats = showtimeSeats;
    }
}
