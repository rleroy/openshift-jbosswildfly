Feature: Cache service

Scenario Outline: Cache service behaviour
Given a cache service
Given loading is <load>
Given building is <build>
Given data is <data>
Given file system data is <fs>
Given memory data is <mem>
 When I access de data
 Then I should get the response <response>
 Then the memory should be <memstatus>
 Then the file system should be <fsstatus>

Examples:
  | load | build | data | fs   | mem  | response | memstatus | fsstatus |
  | fast |  fast |  ok  | good | good |     good |      good |     good |
#  | fast |  fast |  ok  | good | null |     good |      good |     good |
#  | fast |  fast |  ok  | good | expi |     good |      good |     good |
   