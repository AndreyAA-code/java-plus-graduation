package ru.practicum.deserializer;

import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.deserializer.BaseAvroDeserializer;

public class UserActionAvroDeserializer extends BaseAvroDeserializer<UserActionAvro> {
    
    public UserActionAvroDeserializer() {
        super(UserActionAvro.getClassSchema());
    }
}
