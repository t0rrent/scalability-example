package scalability.service;

import jakarta.inject.Inject;
import scalability.mapper.VersionMapper;
import scalability.util.SimpleConfigUtil;

public class VersionMapperService implements VersionService {
	
	private final VersionMapper versionMapper;

	@Inject
	public VersionMapperService(final VersionMapper versionMapper) {
		this.versionMapper = versionMapper;
	}

	@Override
	public String getDatabaseVersion() {
		return versionMapper.getVersion();
	}

	@Override
	public String getBackendId() {
		return SimpleConfigUtil.getConfig("backendId", "no id configured");
	}
	
}
