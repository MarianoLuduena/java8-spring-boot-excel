@integration
Feature: Upload accounts payroll file

  Background:
    * def accountsUrl = baseUrl + '/accounts'

    * def schema =
    """
    {
      "id": "#number",
      "sourceName": "#string",
      "errors": "#[]",
      "processed": "#number"
    }
    """

  Scenario: Upload multipart file
    Given url accountsUrl
    And multipart file file = { read: '../../accounts/sample10.xls' }
    When method POST
    Then status 200
    And match response == schema
    And match response.processed == 10
