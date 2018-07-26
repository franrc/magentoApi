# MagentoApi

BasicMagentoRestApi is a library to access the most basic services of Magento 2 API. 

### How to use

Download via Gradle:

  Add repository

```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

and include the dependency

```
dependencies {
    implementation 'com.github.franrc:MagentoApi:1.0.1'
}
```

or Maven:

  Add repository 
  
```xml
<repositories>
   <repository>
      <id>jitpack.io</id>
      <url>https://jitpack.io</url>
   </repository>
</repositories>
```

and add the dependency

```xml
<dependency>
    <groupId>com.github.franrc</groupId>
    <artifactId>MagentoApi</artifactId>
    <version>1.0</version>
</dependency>
```

Yo can configure Magento data like this : 

```
DKRestService.setIsOnDebug(true);  // If setIsOnDebug is true, retrofit will log request and responses
        
new MagentoRestConfiguration.Builder().setAccessToken("09rxxxxxxxxxxxxxxxxx")
                                      .setAppUrl("http://dev-magentoapp.xxxxxx/rest/..").build();

It's also possible configure the consumerKey and ConsumerSecret constants

```

To retrieve data, the usage is like this: 

```

new MagentoRestService(this).getMe(new ServiceCallbackOnlyOnServiceResults<MagentoResponse<Customer>>() {
    @Override
    public void onResults(MagentoResponse<Customer> results) {
	if(results == null) Log.i("Magento", "null");
	else {
	    if (results.getError() == null)
		Log.i("Magento", results.getData() != null ? results.getData().getFirstname() : "nulo");
	    else {
		Log.i("Magento", "Error: " + results.getError().getError());
	    }
	}
    }
});
 ```

