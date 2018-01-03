package com.standardcheckout.plugin.flow;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.common.truth.Truth;

class FlowContextTest {

	private FlowContext mock;

	@BeforeEach
	void setup() {
		mock = Mockito.spy(FlowContext.class);
	}

	@Test
	void testGetBeanAbsent() {
		Mockito.when(mock.getPlayer()).thenReturn(Optional.empty());
		Mockito.when(mock.getPlayerId()).thenReturn(UUID.randomUUID());
		IllegalStateException expected = Assertions.assertThrows(IllegalStateException.class, () -> mock.getRequiredPlayer());
		Truth.assertThat(expected).hasMessageThat().isEqualTo("Player " + mock.getPlayerId() + " is not online");
	}

}
