<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-2.0.xsd">
    <changeSet author="tlberglund (generated)" id="1292343869599-1">
        <createTable tableName="scinventoryrecord">
            <column name="id" type="INT">
                <constraints nullable="false"/>
            </column>
            <column name="supplierid" type="INT">
                <constraints nullable="false"/>
            </column>
            <column name="pn" type="VARCHAR(50)">
                <constraints nullable="false"/>
            </column>
            <column name="nativepn" type="VARCHAR(50)">
                <constraints nullable="false"/>
            </column>
            <column name="familyid" type="INT"/>
            <column name="availabletosell" type="INT">
                <constraints nullable="false"/>
            </column>
            <column name="minimumorder" type="INT"/>
            <column name="cost" type="VARCHAR(50)">
                <constraints nullable="false"/>
            </column>
            <column name="pref" type="DECIMAL(10,9)">
                <constraints nullable="false"/>
            </column>
            <column name="packageqty" type="INT"/>
            <column name="prefcategory" type="INT">
                <constraints nullable="false"/>
            </column>
            <column name="active" type="INT"/>
            <column name="timestamp" type="DATETIME">
                <constraints nullable="false"/>
            </column>
            <column name="suppliercode" type="VARCHAR(50)">
                <constraints nullable="false"/>
            </column>
            <column name="flags" type="INT">
                <constraints nullable="false"/>
            </column>
            <column name="incontent" type="INT"/>
            <column name="aliases" type="VARCHAR(255)"/>
            <column name="weight" type="DECIMAL(14,8)"/>
            <column name="filteredpn" type="VARCHAR(50)"/>
        </createTable>
    </changeSet>
    <changeSet author="tlberglund (generated)" id="1292343869599-2">
        <createTable tableName="scinventoryrecordparm">
            <column name="inventoryrecordid" type="INT">
                <constraints nullable="false"/>
            </column>
            <column name="parmid" type="INT">
                <constraints nullable="false"/>
            </column>
            <column name="value" type="DECIMAL(20,10)">
                <constraints nullable="false"/>
            </column>
        </createTable>
    </changeSet>
    <changeSet author="tlberglund (generated)" id="1292343869599-3">
        <createTable tableName="sclineitem">
            <column autoIncrement="true" name="id" type="INT">
                <constraints nullable="false"/>
            </column>
            <column name="projectid" type="INT">
                <constraints nullable="false"/>
            </column>
            <column name="rownumber" type="INT">
                <constraints nullable="false"/>
            </column>
            <column name="requestedquantity" type="INT"/>
            <column name="description" type="VARCHAR(1000)"/>
            <column name="requestedpn" type="VARCHAR(1000)"/>
            <column name="requestedmfg" type="VARCHAR(1000)"/>
            <column name="internalcustomerpn" type="VARCHAR(50)"/>
            <column name="parsedfamilyid" type="INT"/>
            <column name="parsedmfgid" type="INT"/>
            <column name="parsedpn" type="VARCHAR(50)"/>
            <column name="scrubstatus" type="INT"/>
            <column name="parsedconfidence" type="INT"/>
            <column name="scrubduration" type="INT"/>
        </createTable>
    </changeSet>
    <changeSet author="tlberglund (generated)" id="1292343869599-4">
        <createTable tableName="sclineiteminventoryrecord">
            <column name="projectid" type="INT"/>
            <column autoIncrement="true" name="id" type="INT">
                <constraints nullable="false"/>
            </column>
            <column name="lineitemid" type="INT">
                <constraints nullable="false"/>
            </column>
            <column name="inventoryrecordid" type="INT">
                <constraints nullable="false"/>
            </column>
            <column name="matchquality" type="DECIMAL(10,9)"/>
        </createTable>
    </changeSet>
    <changeSet author="tlberglund (generated)" id="1292343869599-5">
        <createTable tableName="scpnparseentry">
            <column name="pn" type="VARCHAR(50)"/>
            <column autoIncrement="true" name="id" type="INT">
                <constraints nullable="false"/>
            </column>
            <column name="projectid" type="INT">
                <constraints nullable="false"/>
            </column>
            <column name="rownumber" type="INT">
                <constraints nullable="false"/>
            </column>
        </createTable>
    </changeSet>
    <changeSet author="tlberglund (generated)" id="1292343869599-6">
        <createTable tableName="scpnparseentryparm">
            <column autoIncrement="true" name="id" type="INT">
                <constraints nullable="false"/>
            </column>
            <column name="projectid" type="INT"/>
            <column name="pnparseentryid" type="INT"/>
            <column name="parmid" type="INT"/>
            <column name="valueid" type="INT"/>
            <column name="valuedouble" type="DECIMAL(20,8)"/>
        </createTable>
    </changeSet>
    <changeSet author="tlberglund (generated)" id="1292343869599-7">
        <createTable tableName="scpnqueryentry">
            <column name="pn" type="VARCHAR(50)"/>
            <column autoIncrement="true" name="id" type="INT">
                <constraints nullable="false"/>
            </column>
            <column name="projectid" type="INT">
                <constraints nullable="false"/>
            </column>
            <column name="rownumber" type="INT">
                <constraints nullable="false"/>
            </column>
        </createTable>
    </changeSet>
    <changeSet author="tlberglund (generated)" id="1292343869599-8">
        <createTable tableName="scproject">
            <column autoIncrement="true" name="id" type="INT">
                <constraints nullable="false"/>
            </column>
            <column name="name" type="VARCHAR(50)"/>
            <column name="creationdate" type="DATETIME"/>
            <column name="filetype" type="INT"/>
            <column name="fileheaderrownum" type="INT"/>
            <column name="sheetname" type="VARCHAR(50)"/>
        </createTable>
    </changeSet>
    <changeSet author="tlberglund (generated)" id="1292343869599-9">
        <createTable tableName="scregistrationwarehouse">
            <column autoIncrement="true" name="id" type="INT">
                <constraints nullable="false"/>
            </column>
            <column name="username" type="VARCHAR(50)"/>
            <column name="registrationdate" type="DATETIME"/>
            <column name="registrationip" type="VARCHAR(50)"/>
        </createTable>
    </changeSet>
    <changeSet author="tlberglund (generated)" id="1292343869599-10">
        <createTable tableName="scscrubrecordwarehouse">
            <column autoIncrement="true" name="id" type="INT">
                <constraints nullable="false"/>
            </column>
            <column name="description" type="VARCHAR(1000)"/>
            <column name="pn" type="VARCHAR(1000)"/>
            <column name="timestamp" type="DATETIME"/>
        </createTable>
    </changeSet>
    <changeSet author="tlberglund (generated)" id="1292343869599-11">
        <createTable tableName="scshoppingcart">
            <column autoIncrement="true" name="id" type="INT">
                <constraints nullable="false"/>
            </column>
            <column name="userid" type="INT">
                <constraints nullable="false"/>
            </column>
            <column name="lastmodified" type="DATETIME"/>
            <column name="name" type="VARCHAR(50)"/>
        </createTable>
    </changeSet>
    <changeSet author="tlberglund (generated)" id="1292343869599-12">
        <createTable tableName="scsuppliedparm">
            <column autoIncrement="true" name="id" type="INT">
                <constraints nullable="false"/>
            </column>
            <column name="projectid" type="INT"/>
            <column name="rownumber" type="INT"/>
            <column name="parmid" type="INT"/>
            <column name="valueid" type="INT"/>
            <column name="valuedouble" type="DECIMAL(20,8)"/>
        </createTable>
    </changeSet>
    <changeSet author="tlberglund (generated)" id="1292343869599-13">
        <createTable tableName="scuser">
            <column autoIncrement="true" name="id" type="INT">
                <constraints nullable="false"/>
            </column>
            <column name="username" type="VARCHAR(50)"/>
            <column name="password" type="VARCHAR(50)"/>
            <column name="emailaddress" type="VARCHAR(50)"/>
            <column name="firstname" type="VARCHAR(50)"/>
            <column name="lastname" type="VARCHAR(50)"/>
            <column name="adminprivilege" type="INT"/>
            <column name="temporary" type="INT"/>
            <column name="lastip" type="VARCHAR(50)"/>
            <column name="lastlogin" type="DATETIME"/>
            <column name="cartid" type="INT"/>
            <column name="sortby" type="INT"/>
            <column name="showonlymfgmatches" type="INT"/>
            <column name="showonlynonzeroinventory" type="INT"/>
            <column name="validated" type="INT"/>
            <column name="validationkey" type="VARCHAR(50)"/>
            <column name="optedin" type="INT"/>
            <column name="phonenumber" type="VARCHAR(50)"/>
            <column name="company" type="VARCHAR(50)"/>
        </createTable>
    </changeSet>
    <changeSet author="tlberglund (generated)" id="1292343869599-14">
        <addPrimaryKey columnNames="pn, suppliercode, supplierid" tableName="scinventoryrecord"/>
    </changeSet>
    <changeSet author="tlberglund (generated)" id="1292343869599-15">
        <addPrimaryKey columnNames="inventoryrecordid, parmid" tableName="scinventoryrecordparm"/>
    </changeSet>
    <changeSet author="tlberglund (generated)" id="1292343869599-16">
        <addPrimaryKey columnNames="rownumber, projectid" tableName="sclineitem"/>
    </changeSet>
    <changeSet author="tlberglund (generated)" id="1292343869599-17">
        <addPrimaryKey columnNames="inventoryrecordid, lineitemid" tableName="sclineiteminventoryrecord"/>
    </changeSet>
    <changeSet author="tlberglund (generated)" id="1292343869599-18">
        <addPrimaryKey columnNames="rownumber, projectid" tableName="scpnparseentry"/>
    </changeSet>
    <changeSet author="tlberglund (generated)" id="1292343869599-19">
        <addPrimaryKey columnNames="projectid, rownumber" tableName="scpnqueryentry"/>
    </changeSet>
    <changeSet author="tlberglund (generated)" id="1292343869599-20">
        <createIndex indexName="sys_c0035302" tableName="scinventoryrecord" unique="true">
            <column name="id"/>
        </createIndex>
    </changeSet>
    <changeSet author="tlberglund (generated)" id="1292343869599-21">
        <createIndex indexName="util_db_fk_1669632842" tableName="scinventoryrecord" unique="false">
            <column name="supplierid"/>
        </createIndex>
    </changeSet>
    <changeSet author="tlberglund (generated)" id="1292343869599-22">
        <createIndex indexName="SCInventoryRecordParmindex" tableName="scinventoryrecordparm" unique="false">
            <column name="parmid"/>
            <column name="value"/>
        </createIndex>
    </changeSet>
    <changeSet author="tlberglund (generated)" id="1292343869599-23">
        <createIndex indexName="sys_c0035282" tableName="sclineitem" unique="true">
            <column name="id"/>
        </createIndex>
    </changeSet>
    <changeSet author="tlberglund (generated)" id="1292343869599-24">
        <createIndex indexName="sys_c0035306" tableName="sclineiteminventoryrecord" unique="true">
            <column name="id"/>
        </createIndex>
    </changeSet>
    <changeSet author="tlberglund (generated)" id="1292343869599-25">
        <createIndex indexName="sys_c0035286" tableName="scpnparseentry" unique="true">
            <column name="id"/>
        </createIndex>
    </changeSet>
    <changeSet author="tlberglund (generated)" id="1292343869599-26">
        <createIndex indexName="sys_c0035287" tableName="scpnparseentryparm" unique="true">
            <column name="id"/>
        </createIndex>
    </changeSet>
    <changeSet author="tlberglund (generated)" id="1292343869599-27">
        <createIndex indexName="sys_c0035288" tableName="scpnqueryentry" unique="true">
            <column name="id"/>
        </createIndex>
    </changeSet>
    <changeSet author="tlberglund (generated)" id="1292343869599-28">
        <createIndex indexName="sys_c0035279" tableName="scproject" unique="true">
            <column name="id"/>
        </createIndex>
    </changeSet>
    <changeSet author="tlberglund (generated)" id="1292343869599-29">
        <createIndex indexName="sys_c0035349" tableName="scregistrationwarehouse" unique="true">
            <column name="id"/>
        </createIndex>
    </changeSet>
    <changeSet author="tlberglund (generated)" id="1292343869599-30">
        <createIndex indexName="sys_c0035339" tableName="scscrubrecordwarehouse" unique="true">
            <column name="id"/>
        </createIndex>
    </changeSet>
    <changeSet author="tlberglund (generated)" id="1292343869599-31">
        <createIndex indexName="sys_c0035318" tableName="scshoppingcart" unique="true">
            <column name="id"/>
        </createIndex>
    </changeSet>
    <changeSet author="tlberglund (generated)" id="1292343869599-32">
        <createIndex indexName="sys_c0035284" tableName="scsuppliedparm" unique="true">
            <column name="id"/>
        </createIndex>
    </changeSet>
    <changeSet author="tlberglund (generated)" id="1292343869599-33">
        <createIndex indexName="sys_c0035276" tableName="scuser" unique="true">
            <column name="id"/>
        </createIndex>
    </changeSet>
    <changeSet author="tlberglund (generated)" id="1292343869599-34">
        <createIndex indexName="sys_c0035277" tableName="scuser" unique="true">
            <column name="username"/>
        </createIndex>
    </changeSet>
    <changeSet author="tlberglund (generated)" id="1292343869599-35">
        <createIndex indexName="sys_c0035278" tableName="scuser" unique="true">
            <column name="emailaddress"/>
        </createIndex>
    </changeSet>
    <changeSet author="tlberglund (generated)" id="1292343869599-36">
        <addForeignKeyConstraint baseColumnNames="inventoryrecordid" baseTableName="SCINVENTORYRECORDPARM" baseTableSchemaName="legacy_db" constraintName="SCInventoryRecordParm__SCInventoryRecord_id" deferrable="false" initiallyDeferred="false" onDelete="NO ACTION" onUpdate="NO ACTION" referencedColumnNames="id" referencedTableName="scinventoryrecord" referencedTableSchemaName="legacy_db" referencesUniqueColumn="false"/>
    </changeSet>
    <changeSet author="tlberglund (generated)" id="1292343869599-37">
        <addForeignKeyConstraint baseColumnNames="projectid" baseTableName="SCLINEITEM" baseTableSchemaName="legacy_db" constraintName="SCLineItem__SCProject_id" deferrable="false" initiallyDeferred="false" onDelete="NO ACTION" onUpdate="NO ACTION" referencedColumnNames="id" referencedTableName="scproject" referencedTableSchemaName="legacy_db" referencesUniqueColumn="false"/>
    </changeSet>
    <changeSet author="tlberglund (generated)" id="1292343869599-38">
        <addForeignKeyConstraint baseColumnNames="inventoryrecordid" baseTableName="SCLINEITEMINVENTORYRECORD" baseTableSchemaName="legacy_db" constraintName="SCLineItemInventoryRecord__SCInventoryRecord_id" deferrable="false" initiallyDeferred="false" onDelete="NO ACTION" onUpdate="NO ACTION" referencedColumnNames="id" referencedTableName="scinventoryrecord" referencedTableSchemaName="legacy_db" referencesUniqueColumn="false"/>
    </changeSet>
    <changeSet author="tlberglund (generated)" id="1292343869599-39">
        <addForeignKeyConstraint baseColumnNames="lineitemid" baseTableName="SCLINEITEMINVENTORYRECORD" baseTableSchemaName="legacy_db" constraintName="SCLineItemInventoryRecord__SCLineItem_id" deferrable="false" initiallyDeferred="false" onDelete="NO ACTION" onUpdate="NO ACTION" referencedColumnNames="id" referencedTableName="sclineitem" referencedTableSchemaName="legacy_db" referencesUniqueColumn="false"/>
    </changeSet>
    <changeSet author="tlberglund (generated)" id="1292343869599-40">
        <addForeignKeyConstraint baseColumnNames="projectid" baseTableName="SCLINEITEMINVENTORYRECORD" baseTableSchemaName="legacy_db" constraintName="SCLineItemInventoryRecord__SCProject_id" deferrable="false" initiallyDeferred="false" onDelete="NO ACTION" onUpdate="NO ACTION" referencedColumnNames="id" referencedTableName="scproject" referencedTableSchemaName="legacy_db" referencesUniqueColumn="false"/>
    </changeSet>
    <changeSet author="tlberglund (generated)" id="1292343869599-41">
        <addForeignKeyConstraint baseColumnNames="projectid, rownumber" baseTableName="SCPNPARSEENTRY" baseTableSchemaName="legacy_db" constraintName="SCPNParseEntry__SCLineItem__projectID_rowNumber" deferrable="false" initiallyDeferred="false" onDelete="NO ACTION" onUpdate="NO ACTION" referencedColumnNames="projectid, rownumber" referencedTableName="sclineitem" referencedTableSchemaName="legacy_db" referencesUniqueColumn="false"/>
    </changeSet>
    <changeSet author="tlberglund (generated)" id="1292343869599-42">
        <addForeignKeyConstraint baseColumnNames="pnparseentryid" baseTableName="SCPNPARSEENTRYPARM" baseTableSchemaName="legacy_db" constraintName="SCPNParseEntryParm__SCPNParseEntry_id" deferrable="false" initiallyDeferred="false" onDelete="NO ACTION" onUpdate="NO ACTION" referencedColumnNames="id" referencedTableName="scpnparseentry" referencedTableSchemaName="legacy_db" referencesUniqueColumn="false"/>
    </changeSet>
    <changeSet author="tlberglund (generated)" id="1292343869599-43">
        <addForeignKeyConstraint baseColumnNames="projectid" baseTableName="SCPNPARSEENTRYPARM" baseTableSchemaName="legacy_db" constraintName="SCPNParseEntryParm__SCProject_id" deferrable="false" initiallyDeferred="false" onDelete="NO ACTION" onUpdate="NO ACTION" referencedColumnNames="id" referencedTableName="scproject" referencedTableSchemaName="legacy_db" referencesUniqueColumn="false"/>
    </changeSet>
    <changeSet author="tlberglund (generated)" id="1292343869599-44">
        <addForeignKeyConstraint baseColumnNames="projectid, rownumber" baseTableName="SCPNQUERYENTRY" baseTableSchemaName="legacy_db" constraintName="SCPNQueryEntry__SCLineItem_projectID_rowNumber" deferrable="false" initiallyDeferred="false" onDelete="NO ACTION" onUpdate="NO ACTION" referencedColumnNames="projectid, rownumber" referencedTableName="sclineitem" referencedTableSchemaName="legacy_db" referencesUniqueColumn="false"/>
    </changeSet>
    <changeSet author="tlberglund (generated)" id="1292343869599-45">
        <addForeignKeyConstraint baseColumnNames="userid" baseTableName="SCSHOPPINGCART" baseTableSchemaName="legacy_db" constraintName="SCShoppingCart__SCUser_id" deferrable="false" initiallyDeferred="false" onDelete="NO ACTION" onUpdate="NO ACTION" referencedColumnNames="id" referencedTableName="scuser" referencedTableSchemaName="legacy_db" referencesUniqueColumn="false"/>
    </changeSet>
    <changeSet author="tlberglund (generated)" id="1292343869599-46">
        <addForeignKeyConstraint baseColumnNames="projectid, rownumber" baseTableName="SCSUPPLIEDPARM" baseTableSchemaName="legacy_db" constraintName="SCSuppliedParm__SCLineItem_projectID_rowNumber" deferrable="false" initiallyDeferred="false" onDelete="NO ACTION" onUpdate="NO ACTION" referencedColumnNames="projectid, rownumber" referencedTableName="sclineitem" referencedTableSchemaName="legacy_db" referencesUniqueColumn="false"/>
    </changeSet>
</databaseChangeLog>
