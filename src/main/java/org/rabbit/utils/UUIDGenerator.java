package org.rabbit.utils;

import com.fasterxml.uuid.Generators;

import java.util.UUID;

public class UUIDGenerator {

    public static UUID generate(){
        return Generators.timeBasedGenerator().generate();
    }
}
