package oleg.turyk.test.dto.message;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MessageDto(@JsonProperty String role, @JsonProperty String content) {
}
