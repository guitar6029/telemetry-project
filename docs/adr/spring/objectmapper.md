# Jackson ObjectMapper

Raw JSON

↓

ObjectMapper

↓

Map<String, Object>

Example

Map<String, Object> payload =
objectMapper.readValue(
rawPayload,
new TypeReference<Map<String, Object>>() {}
);

Reason

Java generics are erased at runtime.

TypeReference preserves generic type information.

```
{
  "temperature":23.75,
  "humidity":48,
  "battery_pct":91,
  "engine_running":true,
  "firmware":"1.2.3"
}
```
