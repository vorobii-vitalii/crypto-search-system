## Crypto search platform

Consists of 2 components:
1. Crypto indexer that fetches crypto data from FinnHub REST API and indexes it to ElasticSearch
2. Crypto search service that accepts query and uses ElasticSearch to read all matching cryptocurrencies

## Technology stack:
1. Java 21
2. ELK
3. APM Java Agent
4. gRPC
5. Dagger (for dependency injection)
6. JUnit, Mockito, AssertJ for testing

### View data in index:
```shell
curl http://localhost:9200/cryptos/_search?pretty&q=*:*&size=5000 | jq
```


### Test locally
```shell
grpc-client-cli --proto ~/IdeaProjects/crypto-search-system/crypto-search-service/src/main/proto/crypto-search.proto localhost:8000
```

### Example usage
```shell
Message json (type ? to see defaults): {"query": "bitmex"}
[{
  "symbol": "XRPUSDT",
  "description": "Bitmex XRPUSDT",
  "marketName": "BITMEX"
},
{
  "symbol": "BCHUSDT",
  "description": "Bitmex BCHUSDT",
  "marketName": "BITMEX"
},
{
  "symbol": "ARBUSD",
  "description": "Bitmex ARBUSD",
  "marketName": "BITMEX"
},
{
  "symbol": "ETHU23",
  "description": "Bitmex ETHU23",
  "marketName": "BITMEX"
},
{
  "symbol": "PEPEUSDT",
  "description": "Bitmex PEPEUSDT",
  "marketName": "BITMEX"
},
{
  "symbol": "1TAIDOGEUSDT",
  "description": "Bitmex 1TAIDOGEUSDT",
  "marketName": "BITMEX"
},
{
  "symbol": "ADAU23",
  "description": "Bitmex ADAU23",
  "marketName": "BITMEX"
},
{
  "symbol": "GMXUSDT",
  "description": "Bitmex GMXUSDT",
  "marketName": "BITMEX"
},
{
  "symbol": "BMEXUSDT",
  "description": "Bitmex BMEXUSDT",
  "marketName": "BITMEX"
},
{
  "symbol": "TRX_USDT",
  "description": "Bitmex TRX_USDT",
  "marketName": "BITMEX"
}]
Message json (type ? to see defaults): 
```

## Kibana
http://localhost:5601/

