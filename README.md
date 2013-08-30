# Simulator agent for the Hunger Games

Simulates the departure and arrivals events sent by the [RFID bot](https://github.com/ltg-uic/ltg-rfid-monitoring-bot).

## Events
All the events follow the [LTG Event specification](https://github.com/ltg-uic/hunger-games/wiki#ltg-event-specification).

This is the format of the arrival and departure events. The following example shows that the `id` number `1623110` departed the den (`hg-den`).
```json
{
    "event": "rfid_update",
    "destination": "hg-den",
    "payload": {
        "arrivals": [],
        "departures": [
            "1623110"
        ]
    }
}
```
