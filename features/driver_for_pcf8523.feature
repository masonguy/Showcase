Feature: Driver for pcf8523

    Scenario: I want to access a pcf8523 on NetBSD
      Given a NetBSD user with a pcf8523
      When I plug clock into board
      Then I should see a match or attach call
      And I should see the device present on chip
      
     Scenario: I want to access a pcf8523 on NetBSD
      Given a NetBSD user with a pcf8523
      When I access the gettime on clock
      Then I should be returned the current time
      
