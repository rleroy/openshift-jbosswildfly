Feature: Cache service

Scenario Outline: Cache service behaviour
Given a cache service
Given loading is <load>
Given reading is <read>
Given writing is <write>
Given data is <data>
Given file system data is <fs>
Given memory data is <mem>
 When I access the data
 Then I should get the response <response>
 Then the memory should be <memstatus>
 Then the file system should be <fsstatus>
 Then the number of call to the loader should be <call>

Examples:
  | load | read | write | data | fs    | mem   | response | memstatus | fsstatus | call |
  | fast | fast | fast  | ok   | fresh | fresh | fresh    | fresh     | fresh    | 0    |
  | fast | fast | fast  | ok   | fresh | empty | fresh    | fresh     | fresh    | 0    |
  