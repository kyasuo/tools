## master-watcher tool
Watch master branch updating and create Pull/Reqs merging from master to team branch

#### Make zip file(distribution)
    $ mvn clean package
     [output] target/master-watcher-1.0.0-release.zip

#### Deploy zip file
Deploy zip file and unzip it.

#### Setup
Modify application.properties depending on your environment.

#### Run
    $ sh run.sh [pull/req url] [team branch name]
      <pull/req url> http://[hostname]/api/v3/repos/[group]/[repository]/pulls


##### Jenkins sample
    1) create new job as a free-style project
    2) configuration
      GitBucket
        URL: http://[hostname]/gitbucket/git/[group]/[repository].git
        Build Parameterized:
            BRANCH: branch/XXXX
            API: http://[hostname]/gitbucket/api/v3/repos/[group]/[repository]/pulls
            TOOL: /opt/tool/master-watcher
      Source Code
        Repository URL: http://[hostname]/gitbucket/git/[group]/[repository].git
      Build(Shell Script)
          RESULT=`git diff origin/$BRANCH  origin/master  |wc -l`
          if test $RESULT -gt 0 ; then
            cd $TOOL
            ./run.sh $API $BRANCH
          fi
