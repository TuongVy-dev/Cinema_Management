# TODO

## Showtime cashier pagination (by day)
- [ ] Add repository query to fetch showtimes for a specific date with Pageable (and fetch joins movie/room/template).
- [ ] Update pagination logic for cashier view (paginate by showtime slots within the day).

- [ ] Add service method to return Page of showtime DTOs (or entities) for the selectedDate.
- [ ] Update StaffHomeController#showShowtimesForCashier to support `page` and `size`, defaulting to `date=LocalDate.now()`.
- [ ] Update Thymeleaf template `cashier/showtime_for_cashier.html` to render pagination UI and preserve selectedDate.
- [ ] (Optional) Add pagination params to CashierShowtimeRestController if the frontend uses REST.
- [ ] Run/verify locally: `GET /staffs/cashier/showtimes?date=YYYY-MM-DD&page=1&size=10`.

