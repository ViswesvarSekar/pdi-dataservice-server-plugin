package org.pentaho.di.repository.pur.metastore;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.di.repository.pur.PurRepository;
import org.pentaho.di.repository.pur.PurRepositoryMeta;
import org.pentaho.metastore.api.IMetaStoreAttribute;
import org.pentaho.metastore.api.IMetaStoreElement;
import org.pentaho.metastore.stores.memory.MemoryMetaStoreAttribute;
import org.pentaho.metastore.stores.memory.MemoryMetaStoreElement;
import org.pentaho.platform.api.repository2.unified.IUnifiedRepository;
import org.pentaho.platform.api.repository2.unified.RepositoryFile;
import org.pentaho.platform.api.repository2.unified.data.node.DataNode;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PurRepositoryMetaStoreTest {

  private PurRepositoryMetaStore metaStore;
  private PurRepository purRepository;
  private IUnifiedRepository pur;
  private RepositoryFile namespacesFolder;

  @Before
  public void setUp() throws Exception {
    purRepository = mock( PurRepository.class );
    pur = mock( IUnifiedRepository.class );
    namespacesFolder = mock( RepositoryFile.class );

    when( purRepository.getPur() ).thenReturn( pur );
    when( pur.getFile( PurRepositoryMetaStore.METASTORE_FOLDER_PATH ) ).thenReturn( namespacesFolder );
    when( purRepository.getRepositoryMeta() ).thenReturn( mock( PurRepositoryMeta.class ) );
    when( purRepository.getRepositoryMeta().getName() ).thenReturn( "MockPurRepository" );

    metaStore = new PurRepositoryMetaStore( purRepository );
  }

  @Test
  public void testDataNodeConversion() throws Exception {
    IMetaStoreElement expected = new MemoryMetaStoreElement();

    expected.setName( "parent" );
    expected.addChild( new MemoryMetaStoreAttribute( "date", new Date() ) );
    expected.addChild( new MemoryMetaStoreAttribute( "long", 32L ) );
    expected.addChild( new MemoryMetaStoreAttribute( "double", 3.2 ) );
    expected.addChild( new MemoryMetaStoreAttribute( "string", "value" ) );

    MemoryMetaStoreAttribute collection = new MemoryMetaStoreAttribute( "collection", "collection-value" );
    for ( int i = 0; i < 10; i++ ) {
      collection.addChild( new MemoryMetaStoreAttribute( "key-" + i, "value-" + i ) );
    }
    expected.addChild( collection );

    DataNode dataNode = new DataNode( "test" );
    metaStore.elementToDataNode( expected, dataNode );
    IMetaStoreElement verify = new MemoryMetaStoreElement();
    metaStore.dataNodeToElement( dataNode, verify );

    assertEquals( expected.getName(), verify.getName() );
    validate( expected, verify );
  }

  public static void validate( IMetaStoreAttribute expected, IMetaStoreAttribute verify ) {
    assertEquals( expected.getValue(), verify.getValue() );
    for ( IMetaStoreAttribute attr : expected.getChildren() ) {
      validate( expected.getChild( attr.getId() ), verify.getChild( attr.getId() ) );
    }
  }
}
