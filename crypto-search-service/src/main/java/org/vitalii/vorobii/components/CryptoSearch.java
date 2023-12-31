package org.vitalii.vorobii.components;

import org.vitalii.vorobii.modules.ElasticSearchModule;
import org.vitalii.vorobii.modules.ServerModule;

import dagger.Component;
import io.grpc.Server;

@Component(modules = {ServerModule.class, ElasticSearchModule.class})
public interface CryptoSearch {
	Server server();
}
