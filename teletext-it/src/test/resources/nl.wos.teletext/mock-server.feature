Feature: MockServer testen
  Scenario: I can start a mock server and receive text files
    Given a started mockserver on port 5968
    And I send testData to the mockserver on port 5968
    Then I can retrieve testData from the mockserver on port 5969