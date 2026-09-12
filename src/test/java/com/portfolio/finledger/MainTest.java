package com.portfolio.finledger;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Test de fumée très simple.
 *
 * Ce test ne valide pas encore une règle métier.
 * Il vérifie seulement que le point d'entrée principal
 * peut être appelé sans lever d'exception.
 */
class MainTest {

    @Test
    void mainShouldStartWithoutThrowing() {
        assertDoesNotThrow(() -> Main.main(new String[]{}));
    }
}